package Kong.validationPractice.controller;

import Kong.validationPractice.character.CharacterCreateForm;
import Kong.validationPractice.service.CreateCharacterService;
import Kong.validationPractice.validation.CharacterValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CreateCharacterController {

    private final CreateCharacterService createCharacterService;

    private final CharacterValidation characterValidation;

    @PostMapping(value = "/createCharacter")
    public String createCharacter(@Validated @ModelAttribute(value="character") CharacterCreateForm characterCreateForm,
                                  BindingResult bindingResult,
                                  Model model) {

        characterValidation.validateWithModel(characterCreateForm, bindingResult, model);

        if (bindingResult.hasErrors()) {
            return "/createCharacter";
        }

        createCharacterService.saveCharacter(characterCreateForm.toCharacter());

        return "redirect:/";
    }
}
