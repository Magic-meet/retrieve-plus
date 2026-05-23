package edu.njucm.retrievejava.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.njucm.retrievejava.dao.PaperRepository;
import edu.njucm.retrievejava.dao.ParagraphRepository;
import edu.njucm.retrievejava.es.dao.PaperElasticsearchRepository;
import edu.njucm.retrievejava.es.dao.ParagraphElasticsearchRepository;
import edu.njucm.retrievejava.es.model.PaperES;
import edu.njucm.retrievejava.es.model.ParagraphES;
import edu.njucm.retrievejava.model.Paper;
import edu.njucm.retrievejava.model.PaperAuthor;
import edu.njucm.retrievejava.model.Paragraph;
import edu.njucm.retrievejava.service.RPCService;
import edu.njucm.retrievejava.service.RetrievalService;
import edu.njucm.retrievejava.vo.ChunkVO;
import edu.njucm.retrievejava.vo.RetrievalRequestVO;
import edu.njucm.retrievejava.vo.RetrievalResponseVO;
import edu.njucm.retrievejava.vo.RetrievalResultItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RetrievalServiceImpl implements RetrievalService {

    @Autowired
    private PaperElasticsearchRepository paperElasticsearchRepository;

    @Autowired
    private ParagraphElasticsearchRepository paragraphElasticsearchRepository;

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private ParagraphRepository paragraphRepository;

    @Autowired
    private RPCService rpcService;

    @Override
    public RetrievalResponseVO keywordSearch(RetrievalRequestVO request) throws IOException {
        validateRequest(request);
        Long paperIdFilter = resolvePaperIdFilter(request.getDocumentId());
        List<ParagraphES> matches = paragraphElasticsearchRepository.findByText(request.getQuery());
        Map<Long, Double> scores = new HashMap<>();
        Map<Long, List<ChunkVO>> chunksByPaperId = new HashMap<>();

        int rank = 0;
        for (ParagraphES match : matches) {
            if (paperIdFilter != null && !paperIdFilter.equals(match.getPaperId())) {
                continue;
            }
            double score = reciprocalRank(rank++);
            scores.merge(match.getPaperId(), score, Double::sum);
            chunksByPaperId.computeIfAbsent(match.getPaperId(), ignored -> new ArrayList<>());
            if (chunksByPaperId.get(match.getPaperId()).size() < 3) {
                chunksByPaperId.get(match.getPaperId()).add(toChunkVO(match));
            }
        }

        return buildResponse("keyword", request, scores, chunksByPaperId);
    }

    @Override
    public RetrievalResponseVO semanticSearch(RetrievalRequestVO request) throws IOException {
        validateRequest(request);
        Long paperIdFilter = resolvePaperIdFilter(request.getDocumentId());
        List<Double> queryVector = toVector(request.getQuery());

        Map<Long, Double> scores = new HashMap<>();
        addPaperScores(scores, paperElasticsearchRepository.searchByEmbeddingNLP(queryVector), paperIdFilter, 1.0);

        return buildResponse("semantic", request, scores, new HashMap<>());
    }

    @Override
    public RetrievalResponseVO hybridSearch(RetrievalRequestVO request) throws IOException {
        validateRequest(request);
        RetrievalResponseVO keywordResponse = keywordSearch(request);
        RetrievalResponseVO semanticResponse = semanticSearch(request);

        Map<Long, Double> scores = new HashMap<>();
        Map<Long, List<ChunkVO>> chunksByPaperId = new HashMap<>();

        for (RetrievalResultItemVO item : keywordResponse.getResults()) {
            scores.merge(item.getPaperId(), item.getScore() * 0.5, Double::sum);
            chunksByPaperId.put(item.getPaperId(), item.getChunks());
        }
        for (RetrievalResultItemVO item : semanticResponse.getResults()) {
            scores.merge(item.getPaperId(), item.getScore() * 0.5, Double::sum);
            chunksByPaperId.putIfAbsent(item.getPaperId(), item.getChunks());
        }

        return buildResponse("hybrid", request, scores, chunksByPaperId);
    }

    private void validateRequest(RetrievalRequestVO request) throws IOException {
        if (request == null || request.getQuery() == null || request.getQuery().isBlank()) {
            throw new IOException("query 不能为空");
        }
    }

    private Long resolvePaperIdFilter(Long documentId) throws IOException {
        if (documentId == null) {
            return null;
        }
        Paper paper = paperRepository.findByPaperInfoRelationId(documentId);
        if (paper == null) {
            throw new IOException("指定 documentId 未完成解析或不存在");
        }
        return paper.getPaperId();
    }

    private List<Double> toVector(String query) throws JsonProcessingException {
        return Arrays.stream(rpcService.processString(query)).boxed().collect(Collectors.toList());
    }

    private void addPaperScores(Map<Long, Double> scores, List<PaperES> papers, Long paperIdFilter, double weight) {
        int rank = 0;
        for (PaperES paperES : papers) {
            if (paperIdFilter != null && !paperIdFilter.equals(paperES.getPaperId())) {
                continue;
            }
            scores.merge(paperES.getPaperId(), reciprocalRank(rank++) * weight, Double::sum);
        }
    }

    private double reciprocalRank(int rank) {
        return 1.0 / (rank + 1);
    }

    private RetrievalResponseVO buildResponse(String mode, RetrievalRequestVO request, Map<Long, Double> scores,
                                              Map<Long, List<ChunkVO>> chunksByPaperId) {
        int topK = request.getTopK() == null || request.getTopK() <= 0 ? 5 : request.getTopK();
        List<RetrievalResultItemVO> items = scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topK)
                .map(entry -> toResultItem(entry.getKey(), entry.getValue(), mode, request.getQuery(), chunksByPaperId.get(entry.getKey())))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        RetrievalResponseVO response = new RetrievalResponseVO();
        response.setMode(mode);
        response.setQuery(request.getQuery());
        response.setTotal(items.size());
        response.setResults(items);
        return response;
    }

    private RetrievalResultItemVO toResultItem(Long paperId, Double score, String mode, String query, List<ChunkVO> chunkMatches) {
        Paper paper = paperRepository.findByPaperId(paperId);
        if (paper == null) {
            return null;
        }
        RetrievalResultItemVO item = new RetrievalResultItemVO();
        item.setPaperId(paperId);
        item.setDocumentId(paper.getPaperInfo() != null ? paper.getPaperInfo().getRelationId() : null);
        item.setTitle(paper.getTitle());
        item.setYear(paper.getYear());
        item.setVenue(paper.getVenue());
        item.setAbstractContent(paper.getAbstractContent());
        item.setAuthors(paper.getAuthors().stream()
                .sorted(Comparator.comparingInt(PaperAuthor::getAuthorRank))
                .map(paperAuthor -> paperAuthor.getAuthor().getName())
                .collect(Collectors.joining("; ")));
        item.setScore(score);
        item.setRetrievalType(mode);
        item.setChunks(chunkMatches == null || chunkMatches.isEmpty() ? selectFallbackChunks(paper, query) : chunkMatches);
        return item;
    }

    private List<ChunkVO> selectFallbackChunks(Paper paper, String query) {
        List<Paragraph> paragraphs = paragraphRepository.findAllByPaper(paper);
        String lowerQuery = query.toLowerCase(Locale.ROOT);
        List<ChunkVO> matched = paragraphs.stream()
                .filter(paragraph -> paragraph.getText() != null && paragraph.getText().toLowerCase(Locale.ROOT).contains(lowerQuery))
                .limit(3)
                .map(this::toChunkVO)
                .collect(Collectors.toList());
        if (!matched.isEmpty()) {
            return matched;
        }
        return paragraphs.stream().limit(3).map(this::toChunkVO).collect(Collectors.toList());
    }

    private ChunkVO toChunkVO(ParagraphES paragraphES) {
        ChunkVO chunkVO = new ChunkVO();
        chunkVO.setChunkId(paragraphES.getParagraphId());
        chunkVO.setSection(paragraphES.getSection());
        chunkVO.setSecNum(paragraphES.getSecNum());
        chunkVO.setType(paragraphES.getType());
        chunkVO.setText(paragraphES.getText());
        return chunkVO;
    }

    private ChunkVO toChunkVO(Paragraph paragraph) {
        ChunkVO chunkVO = new ChunkVO();
        chunkVO.setChunkId(paragraph.getParagraphId());
        chunkVO.setSection(paragraph.getSection());
        chunkVO.setSecNum(paragraph.getSecNum());
        chunkVO.setType(paragraph.getType());
        chunkVO.setText(paragraph.getText());
        return chunkVO;
    }
}
