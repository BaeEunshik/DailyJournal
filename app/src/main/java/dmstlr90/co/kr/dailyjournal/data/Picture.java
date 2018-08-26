package dmstlr90.co.kr.dailyjournal.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Picture {
    private Integer id;
    private Integer addr;
    private Integer width;
    private Integer height;
    private Integer journal_id;
    private Integer date;
}
