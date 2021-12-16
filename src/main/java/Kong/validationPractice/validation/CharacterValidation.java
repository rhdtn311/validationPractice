package Kong.validationPractice.validation;

import Kong.validationPractice.character.CharacterCreateForm;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
public class CharacterValidation implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Character.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
    }

    public void validateWithModel(Object target, Errors errors, Model model) {

        CharacterCreateForm characterCreateForm = (CharacterCreateForm) target;
        BindingResult bindingResult = (BindingResult) errors;

        // 검증 로직
//         1. 이름을 입력하지 않거나 이름의 길이가 5자 ~ 15자가 아니면 에러 (기본 BindingResult 사용)
        if (!StringUtils.hasText(characterCreateForm.getName()) || characterCreateForm.getName().length() < 2 || characterCreateForm.getName().length() > 15) {
            bindingResult.addError(new FieldError("characterCreateForm", "name", characterCreateForm.getName(),
                    false, null, null, "이름은 2~15자를 입력해주세요."));

            model.addAttribute("nameError", bindingResult.getFieldError("name").getDefaultMessage());
        }

        // 3. 나이를 입력하지 않거나 나이가 범위를 초과할 경우 에러 (기본 BindingResult 사용)
        if (characterCreateForm.getAge() == null || characterCreateForm.getAge() < 15 || characterCreateForm.getAge() > 100) {
            bindingResult.addError(new FieldError("characterCreateForm", "age", characterCreateForm.getAge()
                    , false, null, null, "나이는 15세 ~ 100세 사이만 등록 가능합니다."));

            model.addAttribute("ageError", bindingResult.getFieldError("age").getDefaultMessage());
        }

        // 키의 범위 (0~250)을 벗어나거나 값을 입력하지 않을 경우 에러 (rejectValue 사용)
        if (characterCreateForm.getHeight() == null || characterCreateForm.getHeight() > 250 || characterCreateForm.getHeight() < 0) {
            bindingResult.rejectValue("height", "range", new Object[]{0, 250}, null);
        }

        // 몸무게의 범위 (0~200)을 벗어나거나 값을 입력하지 않을 경우 (rejectValue 사용)
        if (characterCreateForm.getWeight() == null || characterCreateForm.getWeight() < 0 || characterCreateForm.getWeight() > 200) {
            bindingResult.rejectValue("weight", "range", new Object[]{0, 200}, null);
        }

        // 이메일 형식을 맞춰야 함
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        if (!p.matcher(characterCreateForm.getEmail()).matches()) {
            bindingResult.rejectValue("email", "form");

            model.addAttribute("emailError", true);
        }

        if (characterCreateForm.getWeight() != null && characterCreateForm.getHeight() != null ) {
            if (characterCreateForm.getWeight() / Math.pow((double)characterCreateForm.getHeight() / 100, 2) > 25) {
                bindingResult.reject("overWeight");
                model.addAttribute("globalError", bindingResult.getGlobalError());
            }
        }
    }

}
