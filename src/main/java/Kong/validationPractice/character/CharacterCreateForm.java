package Kong.validationPractice.character;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CharacterCreateForm {

    // weight / height^2가 25보다 크면 등록 불가능 (글로벌 오류)

    // not null
    // 3 ~ 15
    private String name;

    // not null
    // 15 ~ 100
    private Integer age;

    // not null
    private Integer weight;

    // not null
    private Integer height;

    // email 형식을 맞추어야 함
    private String email;

    public Character toCharacter() {

        return Character.builder()
                .name(this.name)
                .age(this.age)
                .height(this.height)
                .weight(this.weight)
                .email(this.email)
                .build();
    }
}
