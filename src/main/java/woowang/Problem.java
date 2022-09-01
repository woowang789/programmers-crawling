package woowang;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Problem {
     int id;
     String title;
     String url;
     String level;

     public Problem(int id, String title, String url, String level) {
          this.id = id;
          this.title = title;
          this.url = url;
          this.level = level;
     }
}
