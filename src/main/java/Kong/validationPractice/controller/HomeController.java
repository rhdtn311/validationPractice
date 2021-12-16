package Kong.validationPractice.controller;

import Kong.validationPractice.character.CharacterCreateForm;
import Kong.validationPractice.service.CharacterListService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final CharacterListService characterListService;

    @RequestMapping("/")
    public String home(Model model) {

        model.addAttribute("characterList", characterListService.characterList());
        return "/home";
    }

    @RequestMapping("/createCharacter")
    public String createCharacter(Model model) {
        model.addAttribute("character", new CharacterCreateForm());
        return "/createCharacter";
    }
}
