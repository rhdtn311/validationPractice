package Kong.validationPractice.character;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CharacterCreateForm {

    // weight / height^2가 25보다 크면 등록 불가능 (글로벌 오류)

    // not null
    // 2 ~ 15
    @NotNull
    @Length(min = 2, max = 15)
    private String name;

    // not null
    // 15 ~ 100
    @NotNull
    @Range(min = 15, max = 100)
    private Integer age;

    // not null
    @NotNull
    @Range(min = 0, max = 200)
    private Integer weight;

    // not null
    @NotNull
    @Range(min = 0, max = 250)
    private Integer height;

    // email 형식을 맞추어야 함
    @Email
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
