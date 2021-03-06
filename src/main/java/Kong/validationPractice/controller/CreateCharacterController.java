package Kong.validationPractice.controller;

import Kong.validationPractice.character.CharacterCreateForm;
import Kong.validationPractice.service.CreateCharacterService;
import Kong.validationPractice.validation.CharacterValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CreateCharacterController {

    private final CreateCharacterService createCharacterService;

    private final CharacterValidation characterValidation;

    @PostMapping(value = "/createCharacterV1")
    public String createCharacterUseBasicValidation(@Validated @ModelAttribute(value="character") CharacterCreateForm characterCreateForm,
                                  BindingResult bindingResult,
                                  Model model) {

        characterValidation.validateWithModel(characterCreateForm, bindingResult, model);

        if (bindingResult.hasErrors()) {
            return "createCharacterV1";
        }

        createCharacterService.saveCharacter(characterCreateForm.toCharacter());

        return "redirect:/";
    }

    @PostMapping("/createCharacterV2")
    public String createCharacterUseBeanValidation(
            @Validated @ModelAttribute(value="character") CharacterCreateForm characterCreateForm,
            BindingResult bindingResult) {

        if (characterCreateForm.getHeight() != null && characterCreateForm.getWeight() != null) {
            if (characterCreateForm.getWeight() / Math.pow((double)characterCreateForm.getHeight() / 100, 2) > 25) {
                bindingResult.reject("overWeight");
            }
        }

        if (bindingResult.hasErrors()) {
            return "createCharacterV2";
        }

        createCharacterService.saveCharacter(characterCreateForm.toCharacter());

        return "redirect:/";
    }
}
