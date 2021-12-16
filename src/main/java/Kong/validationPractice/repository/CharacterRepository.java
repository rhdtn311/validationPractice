package Kong.validationPractice.repository;

import Kong.validationPractice.character.Character;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class CharacterRepository {

    private static final Map<Long, Character> store = new ConcurrentHashMap<>();
    private static long sequence = 0L;

    public void save(Character character) {
        if (character == null) {
            return;
        }

        store.put(sequence++, character);
        log.info("repository = {}", store);
    }

    public Character findById(Long id) {
        return store.get(id);
    }

    public List<Character> findAll() {
        return new ArrayList<>(store.values());
    }

}
