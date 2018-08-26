package dmstlr90.co.kr.dailyjournal.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class Journal {
    private Integer id;
    private String content;
    private Integer date;
    //private Integer tagKey;
}
