package Kong.validationPractice.controller;

import Kong.validationPractice.character.CharacterCreateForm;
import Kong.validationPractice.service.CharacterListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final CharacterListService characterListService;

    @RequestMapping("/")
    public String home(Model model) {

        model.addAttribute("characterList", characterListService.characterList());
        return "/home";
    }

    @RequestMapping(value = "/createCharacterV1", method = RequestMethod.GET)
    public String createCharacter(Model model) {
        model.addAttribute("character", new CharacterCreateForm());
        return "createCharacterV1";
    }

    @RequestMapping(value = "/createCharacterV2", method = RequestMethod.GET)
    public String createCharacterV2(Model model) {
        model.addAttribute("character", new CharacterCreateForm());
        return "createCharacterV2";
    }
}
