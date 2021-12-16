package Kong.validationPractice.service;

import Kong.validationPractice.character.Character;
import Kong.validationPractice.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CharacterListService {

    private final CharacterRepository characterRepository;

    public List<Character> characterList() {
        return characterRepository.findAll();
    }
}
