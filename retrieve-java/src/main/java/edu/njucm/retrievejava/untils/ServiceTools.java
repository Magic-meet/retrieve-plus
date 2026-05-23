package edu.njucm.retrievejava.untils;

import edu.njucm.retrievejava.dao.PaperRepository;
import edu.njucm.retrievejava.es.model.PaperES;
import edu.njucm.retrievejava.model.Paper;
import edu.njucm.retrievejava.model.PaperAuthor;
import edu.njucm.retrievejava.model.Paragraph;
import edu.njucm.retrievejava.vo.PaperVO;
import edu.njucm.retrievejava.vo.ParagraphVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class ServiceTools {



    public static List<ParagraphVO> convertToParagraphVOList(List<Paragraph> paragraphList) {
         List<Paragraph> filteredParagraphList = new ArrayList<>();
        for (Paragraph paragraph : paragraphList) {
            if (!paragraph.getSecNum().equals("null")) {
                filteredParagraphList.add(paragraph);
            }
        }

        // 对剩余的段落按照浮点数的大小进行排序
        Collections.sort(filteredParagraphList, Comparator.comparingDouble(Paragraph::getSecNumAsDouble));

        // 创建ParagraphVO列表
        List<ParagraphVO> paragraphVOList = new ArrayList<>();

        // 遍历排序后的段落列表，创建ParagraphVO对象，并添加到列表中
        String currentSecNum = null;
        StringBuilder currentText = new StringBuilder();
        for (Paragraph paragraph : filteredParagraphList) {
            String secNum = paragraph.getSecNum();
            String text = paragraph.getText();
            // 如果当前的secNum与之前不同，则创建ParagraphVO对象并添加到列表中
            if (!secNum.equals(currentSecNum)) {
                if (currentSecNum != null) {
                    paragraphVOList.add(new ParagraphVO(paragraph.getType(), paragraph.getSection(), currentSecNum, currentText.toString()));
                }
                currentSecNum = secNum;
                currentText = new StringBuilder(text);
            } else {
                // 如果相同，则将文本追加到当前文本后面，用换行符分隔
                currentText.append("\n\n").append(text);
            }
        }
        // 添加最后一个段落
        if (currentSecNum != null) {
            paragraphVOList.add(new ParagraphVO(filteredParagraphList.get(filteredParagraphList.size() - 1).getType(), filteredParagraphList.get(filteredParagraphList.size() - 1).getSection(), currentSecNum, currentText.toString()));
        }

        return paragraphVOList;
    }


    // 根据secNum在ParagraphES列表中查找对应的ParagraphES对象
    private static Paragraph findParagraphBySecNum(List<Paragraph> paragraphList, String secNum) {
        for (Paragraph paragraph : paragraphList) {
            if (paragraph.getSecNum().equals(secNum)) {
                return paragraph;
            }
        }
        return null;
    }

    public static <T> List<T> paginate(List<T> originalList, int pageSize, int pageNumber) {
        int fromIndex = (pageNumber - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, originalList.size());
        if (fromIndex >= originalList.size()) {
            return List.of(); // Return an empty list if the page number is out of bounds
        }
        return originalList.subList(fromIndex, toIndex);
    }
}
