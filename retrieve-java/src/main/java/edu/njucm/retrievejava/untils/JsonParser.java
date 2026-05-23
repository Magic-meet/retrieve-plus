package edu.njucm.retrievejava.untils;

import com.fasterxml.jackson.databind.JsonNode;
import edu.njucm.retrievejava.model.*;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonParser {
    private static String asText(JsonNode node) {
        return node == null || node.isNull() ? "" : node.asText();
    }

    private static int asInt(JsonNode node) {
        return node == null || node.isNull() ? 0 : node.asInt();
    }

    /***
     * 返回作者信息
     * @param root JsonNode根
     * @return List
     */
    public static List<Author> extractAuthors(JsonNode root) {
        List<Author> authors = new ArrayList<>();
        JsonNode authorsNode = root.get("authors");
        if (authorsNode != null && authorsNode.isArray()) {
            for (JsonNode authorNode : authorsNode) {
                String firstName = authorNode.get("first") != null ? authorNode.get("first").asText() : "";
                String lastName = authorNode.get("last") != null ? authorNode.get("last").asText() : "";
                String middleName = getMiddleName(authorNode.get("middle"));
                String email = authorNode.get("email") != null ? authorNode.get("email").asText() : "";
                JsonNode affiliationNode = authorNode.get("affiliation");
                String laboratory = affiliationNode != null && affiliationNode.get("laboratory") != null ? affiliationNode.get("laboratory").asText() : "";
                String institution = affiliationNode != null && affiliationNode.get("institution") != null ? affiliationNode.get("institution").asText() : "";
                String location = affiliationNode != null ? getLocationString(affiliationNode.get("location")) : "";
                String authorName = String.join(" ", List.of(firstName, middleName, lastName).stream()
                        .filter(StringUtils::hasText)
                        .toList());
                if (!StringUtils.hasText(authorName)) {
                    continue;
                }
                Author author = new Author();
                author.setName(authorName);
                if (!laboratory.isEmpty() && !institution.isEmpty())
                    author.setInstitution(laboratory + "|" + institution);
                else
                    author.setInstitution(laboratory + institution);
                author.setEmail(email);
                author.setLocation(location);
                authors.add(author);
            }
        }
        return authors;
    }

    /***
     * 处理姓名的中间部分
     * @param middleNode
     * @return
     */
    private static String getMiddleName(JsonNode middleNode) {
        StringBuilder middleNameBuilder = new StringBuilder();
        if (middleNode != null && middleNode.isArray()) {
            for (JsonNode nameNode : middleNode) {
                middleNameBuilder.append(nameNode.asText()).append(" ");
            }
        }
        String middleName = middleNameBuilder.toString().trim();
        return middleName.isEmpty() ? "" : middleName;
    }

    /***
     * 处理地址的字符串
     * @param locationNode
     * @return
     */
    private static String getLocationString(JsonNode locationNode) {
        StringBuilder locationBuilder = new StringBuilder();
        if (locationNode != null && locationNode.isObject()) {
            locationNode.fields().forEachRemaining(entry -> {
                locationBuilder.append(entry.getValue().asText()).append(", ");
            });
        }
        String location = locationBuilder.toString();
        return StringUtils.hasText(location) ? location.substring(0, location.length() - 2) : "";
    }

    /***
     * 解析论文
     * @param root
     * @return
     */
    public static Paper extractPaper(JsonNode root) {
        Paper paper = new Paper();
        // 提取标题、关键词和摘要
        String title = asText(root.get("title"));
        String keywords = getKeywordsString(root.get("keywords"));
        String abstractContent = asText(root.get("abstract"));
        // 设置 Paper 对象属性
        paper.setTitle(title);
        paper.setKeywords(keywords);
        paper.setAbstractContent(abstractContent);

        return paper;
    }

    /***
     * 处理关键词数组
     * @param keywordsNode
     * @return
     */
    private static String getKeywordsString(JsonNode keywordsNode) {
        StringBuilder keywordsBuilder = new StringBuilder();
        if (keywordsNode != null && keywordsNode.isArray()) {
            for (JsonNode keyword : keywordsNode) {
                keywordsBuilder.append(keyword.asText()).append(";");
            }
        }
        String keywords = keywordsBuilder.toString();
        return keywords.isEmpty() ? "" : keywords.substring(0, keywords.length() - 1);
    }

    /***
     * 处理段落信息
     * @param root
     * @return
     */
    public static Map<String,Object> extractParagraphs(JsonNode root) {
        Map<String, Object> objectMap = new HashMap<>();
        List<Paragraph> paragraphs = new ArrayList<>();
        List<ParagraphReference> paragraphReferences = new ArrayList<>();

        // 提取摘要段落信息
        JsonNode pdfParseNode = root.get("pdf_parse");
        Map<String, Object> abstractInfo = extractParagraphList(pdfParseNode != null ? pdfParseNode.get("abstract") : null, "Abstract");
        paragraphs.addAll((List<Paragraph>) abstractInfo.get("paragraphs"));
        paragraphReferences.addAll((List<ParagraphReference>) abstractInfo.get("paragraphReferences"));

        // 提取正文段落信息
        Map<String, Object> bodyTextInfo = extractParagraphList(pdfParseNode != null ? pdfParseNode.get("body_text") : null, "BodyText");
        paragraphs.addAll((List<Paragraph>) bodyTextInfo.get("paragraphs"));
        paragraphReferences.addAll((List<ParagraphReference>) bodyTextInfo.get("paragraphReferences"));

        // 提取后记段落信息
        Map<String, Object> backMatterInfo = extractParagraphList(pdfParseNode != null ? pdfParseNode.get("back_matter") : null, "BackMatter");
        paragraphs.addAll((List<Paragraph>) backMatterInfo.get("paragraphs"));
        paragraphReferences.addAll((List<ParagraphReference>) backMatterInfo.get("paragraphReferences"));

        objectMap.put("paragraphs", paragraphs);
        objectMap.put("paragraphReferences", paragraphReferences);

        return objectMap;
    }
    private static Map<String, Object> extractParagraphList(JsonNode parentNode, String type) {
        Map<String, Object> objectMap = new HashMap<>();
        List<Paragraph> paragraphs = new ArrayList<>();
        List<ParagraphReference> paragraphReferences = new ArrayList<>();
        if (parentNode != null && parentNode.isArray()) {
            for (JsonNode paragraphNode : parentNode) {
                Paragraph paragraph = new Paragraph();
                paragraph.setType(type);
                paragraph.setSection(asText(paragraphNode.get("section")));
                paragraph.setSecNum(asText(paragraphNode.get("sec_num")));
                paragraph.setText(asText(paragraphNode.get("text")));
                paragraphs.add(paragraph);
                JsonNode citeSpansNode = paragraphNode.get("cite_spans");
                if (citeSpansNode != null && citeSpansNode.isArray()) {
                    for (JsonNode citeSpanNode : citeSpansNode) {
                        int start = asInt(citeSpanNode.get("start"));
                        int end = asInt(citeSpanNode.get("end"));
                        String refText = asText(citeSpanNode.get("text"));
                        ParagraphReference paragraphReference = new ParagraphReference();
                        paragraphReference.setText(refText);
                        paragraphReference.setStart(start);
                        paragraphReference.setEnd(end);
                        paragraphReference.setParagraph(paragraph);
                        paragraphReferences.add(paragraphReference);
                    }
                }
            }
        }
        objectMap.put("paragraphs",paragraphs);
        objectMap.put("paragraphReferences",paragraphReferences);
        return objectMap;
    }
    public static Map<String,Object> PaperReferences(JsonNode root) {
        Map<String, Object> objectMap = new HashMap<>();
        List<PaperReference> paperReferences = new ArrayList<>();
        List<Reference> references = new ArrayList<>();
        objectMap.put("references", references);
        objectMap.put("paperReferences", paperReferences);
        JsonNode pdfParseNode = root.get("pdf_parse");
        JsonNode bibEntriesNode = pdfParseNode != null ? pdfParseNode.get("bib_entries") : null;
        if (bibEntriesNode != null && bibEntriesNode.isObject()) {
            int refNum = 1; // 计数器
            for (JsonNode entryNode : bibEntriesNode) {
                String rawText = asText(entryNode.get("raw_text"));

                // 构建论文-引用关系对象
                PaperReference paperReference = new PaperReference();
                paperReference.setRefNum(String.valueOf(refNum++));
                paperReference.setRawText(rawText);
                String title = asText(entryNode.get("title"));
                int year = asInt(entryNode.get("year"));
                String venue = asText(entryNode.get("venue"));

                // 处理作者信息
                JsonNode authorsNode = entryNode.get("authors");
                String authors = getAuthorsString(authorsNode);

                // 构建引用文献对象
                Reference reference = new Reference();
                reference.setTitle(title);
                reference.setAuthors(authors);
                reference.setYear(String.valueOf(year));
                reference.setVenue(venue);
                references.add(reference);
                paperReference.setReference(reference);
                paperReferences.add(paperReference);
            }
        }
        return objectMap;
    }
    private static String getAuthorsString(JsonNode authorsNode) {
        List<String> authorNames = new ArrayList<>();
        if (authorsNode != null && authorsNode.isArray()) {
            for (JsonNode authorNode : authorsNode) {
                String firstName = authorNode.get("first") != null ? authorNode.get("first").asText() : "";
                String lastName = authorNode.get("last") != null ? authorNode.get("last").asText() : "";
                String authorName = (firstName + " " + lastName).trim();
                if (!authorName.isEmpty()) {
                    authorNames.add(authorName);
                }
            }
        }
        return String.join("; ", authorNames);
    }
    public static void updateParagraphReferences(List<PaperReference> paperReferences,List<ParagraphReference> paragraphReferences) {
        // 正则表达式匹配数字
        Pattern pattern = Pattern.compile("\\d+");

        for (ParagraphReference paragraphReference : paragraphReferences) {
            String text = paragraphReference.getText();
            if (!StringUtils.hasText(text)) {
                continue;
            }
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                int refNum = Integer.parseInt(matcher.group()); // 获取引用编号
                for (PaperReference paperReference : paperReferences) {
                    if (paperReference.getRefNum().equals(String.valueOf(refNum))) {
                        paragraphReference.setReference(paperReference.getReference());
                    }
                }
            }
        }
    }
}
