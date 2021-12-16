package Kong.validationPractice.character;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Character {

    // weight / height^2가 25보다 크면 등록 불가능

    private int id;

    // not null
    // 5 ~ 15
    private String name;

    // not null
    // 15 ~ 100
    private Integer age;

    // not null
    // 0 ~ 200
    private Integer weight;

    // not null
    // 0 ~ 250
    private Integer height;

    // email 형식을 맞추어야 함
    private String email;

}
