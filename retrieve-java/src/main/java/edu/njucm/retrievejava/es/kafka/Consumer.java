package edu.njucm.retrievejava.es.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.njucm.retrievejava.es.dao.PaperElasticsearchRepository;
import edu.njucm.retrievejava.es.dao.ParagraphElasticsearchRepository;
import edu.njucm.retrievejava.es.model.PaperES;
import edu.njucm.retrievejava.es.model.ParagraphES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
public class Consumer {

    @Autowired
    ParagraphElasticsearchRepository paragraphElasticsearchRepository;

    @Autowired
    PaperElasticsearchRepository paperElasticsearchRepository;
    @KafkaListener(topics = "canal")
    public void sync(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(message);
        JsonNode dataNode = rootNode.get("data");
        String table =rootNode.get("table").asText();
        if (table.equals("paragraph")){
            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode paragraphNode : dataNode) {
                    Long paragraphId = paragraphNode.get("paragraph_id").asLong();
                    switch (rootNode.get("type").asText()) {
                        case "INSERT":
                            Long paperId = paragraphNode.get("paper_id").asLong();
                            String secNum = paragraphNode.get("sec_num").asText();
                            String section = paragraphNode.get("section").asText();
                            String type = paragraphNode.get("type").asText();
                            String text = paragraphNode.get("text").asText();
                            ParagraphES paragraphES = new ParagraphES(null,paragraphId,type,section,secNum,text,paperId);
                            paragraphElasticsearchRepository.save(paragraphES);
                            break;
                        // Add cases for other types if needed
                        case "DELETE":
                            paragraphElasticsearchRepository.deleteByParagraphId(paragraphId);
                            break;
                        default:
                            // Handle unknown type
                            break;
                    }
                }
            }
        }else if(table.equals("paper")){
            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode paperNode : dataNode) {
                    Long paperId = paperNode.get("paper_id").asLong();
                    switch (rootNode.get("type").asText()) {
                        case "INSERT":
                            String abstractContent = paperNode.get("abstract_content").asText();
                            String keywords = paperNode.get("keywords").asText();
                            String title = paperNode.get("title").asText();
                            String venue = paperNode.get("venue").asText();
                            String year = paperNode.get("year").asText();
                            PaperES paperES = paperElasticsearchRepository.findByPaperId(paperId);
                            if (paperES == null) {
                                paperES = new PaperES(null,paperId,title,year,venue,keywords,abstractContent,null);
                            }else {
                                paperES.setPaperId(paperId);
                                paperES.setTitle(title);
                                paperES.setAbstractContent(abstractContent);
                                paperES.setKeywords(keywords);
                                paperES.setVenue(venue);
                                paperES.setYear(year);
                            }
                            paperElasticsearchRepository.save(paperES);
                            break;
                        case "DELETE":
                            paperElasticsearchRepository.deleteByPaperId(paperId);
                        // Add cases for other types if needed
                        default:
                            // Handle unknown type
                            break;
                    }
                }
            }
        }
    }

    @KafkaListener(topics = "vector")
    public void get_vector(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(message);
        Long paperId = Long.parseLong(rootNode.get("paper_id").asText());
        // 判断另一Canal消费者是否已完成消费
        PaperES paperES = paperElasticsearchRepository.findByPaperId(paperId);
        if (paperES == null){
            paperES = new PaperES();
        }
        JsonNode embeddingNode = rootNode.get("embedding");
        Double[] embedding = new Double[embeddingNode.size()];
        for (int i = 0; i < embeddingNode.size(); i++) {
            embedding[i] = embeddingNode.get(i).asDouble();
        }
        paperES.setPaperId(paperId);
        paperES.setEmbedding(embedding);
        paperElasticsearchRepository.save(paperES);
    }

}
