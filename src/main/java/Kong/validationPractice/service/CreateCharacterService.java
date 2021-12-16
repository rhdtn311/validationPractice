package Kong.validationPractice.service;

import Kong.validationPractice.character.Character;
import Kong.validationPractice.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CreateCharacterService {

    private final CharacterRepository characterRepository;

    public void saveCharacter(Character character) {
        characterRepository.save(character);
    }
}