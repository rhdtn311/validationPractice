스프링 부트에서 검증 기능을 수행하는 BindingResult를 이해하고자 간단한 미니 프로젝트를 진행하였다.

# 1. BindingResult에 Error를 직접 삽입

**프로젝트 구성**

- SpringMVC 5.3.13
- ThymeLeaf 2.6.1
- Lombok

```bash
├─main
│  ├─generated
│  ├─java
│  │  └─Kong
│  │      └─validationPractice
│  │          │  ValidationPracticeApplication.java
│  │          │
│  │          ├─character
│  │          │      Character.java
│  │          │      CharacterCreateForm.java
│  │          │
│  │          ├─controller
│  │          │      CreateCharacterController.java
│  │          │      HomeController.java
│  │          │
│  │          ├─repository
│  │          │      CharacterRepository.java
│  │          │
│  │          ├─service
│  │          │      CharacterListService.java
│  │          │      CreateCharacterService.java
│  │          │
│  │          └─validation
│  │                  CharacterValidation.java
│  │
│  └─resources
│      │  application.properties
│      │  errors.properties
│      │
│      ├─static
│      └─templates
│              createCharacter.html
│              home.html
```



### 내용

메인 화면은 다음과 같다.

![image](https://user-images.githubusercontent.com/68289543/146318614-152594ba-3e11-443f-b46b-a0c5ada83c9f.png)

캐릭터를 생성할 수 있고 생성한 캐릭터가 메인화면에 표시된다.

![image](https://user-images.githubusercontent.com/68289543/146326905-a042a072-fafc-43a8-a5c9-dd7ffc301261.png)

캐릭터를 생성하는 화면은 위와 같이 간단한 HTML Form 형식이다. 캐릭터가 정상적으로 등록되었다면 홈 화면으로 리다이렉트 되고, 입력값이 검증에 실패했다면 입력 값을 유지한 채 다시 해당 Form 화면이 보여진다.

검증은 다음과 같이 진행된다.

- 이름
  - 어떤 값이라도 입력해야 한다. (not null)
  - 2자 ~ 15자 사이어야 한다.
- 나이
  - 어떤 값이라도 입력해야 한다. (not null)
  - 15세 ~ 100세 사이어야 한다.
- 키
  - 어떤 값이라도 입력해야 한다. (not null)
  - 0cm보다 크고 250cm 보다 작아야 한다.
- 몸무게
  - 어떤 값이라도 입력해야 한다. (not null)
  - 0kg보다 크고 200kg보다 작아야 한다.
- 이메일
  - 이메일 형식을 지켜야 한다.

- 글로벌 검증
  - 몸무게 / 키의 제곱(m^2)은 25를 넘지 않아야 한다.

### 코드

우선 검증의 기본인 더 잘 이해하기 위해 어노테이션 방식의 BeanValidation이 아닌 BindingResult에 Error를 직접 추가하는 방식으로 진행하였다.

**Character.java**

```java
package Kong.validationPractice.character;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Character {

    // weight / height^2가 25보다 크면 등록 불가능

    private int id;

    // not null
    // 2 ~ 15
    private String name;

    // not null
    // 15 ~ 100
    private Integer age;

    // not null
    // 0 ~ 200
    private Integer weight;

    // not null
    // 0 ~ 250
    private Integer height;

    // email 형식을 맞추어야 함
    private String email;

}

```

Form 방식으로 데이터를 전송할 `Character` 객체



**CharacterCreateForm.java**

```java
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
```

Controller에서 `@ModelAttribute`로 Form 데이터를 바인딩할 때 사용할 `dto`



**CharacterRepository**

```java
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
```

`Map` 자료구조를 통해 `Character`를 저장할 Repository. 객체를 저장하는 메서드 (`save()`), `id` 값을 통해 특정 객체를 찾는 메서드 (`findById()`), 저장된 모든 객체를 찾는 메서드(`findAll()`)를 작성하였다.



**CharacterListService.java**

```java
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
```

홈 화면에 출력하기 위해 Repository에 저장되어 있는 모든 `Character` 객체를 가져오는 Service



**CreateCharacterService.java**

```java
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
```

Repository에 생성한 `Character`를 저장하기 위한 Service



**HomeController**

```java
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
```

기본 페이지는 `home.html`이다. 해당 페이지에 접속했을 때 CharacterListService에서 모든 `Character` 객체를 가져와 출력해준다.

`/createCharacter`는 캐릭터를 생성하는 폼으로 이동하는 url인데 만약 입력 폼에 검증이 실패한 값을 입력했을 때 해당 값을 입력 폼에 그대로 표시해야 하기 때문에 (`input value` 옵션) 빈 `CharacterCreateForm`을 넣어두지 않으면 바인딩 에러가 발생한다.



**CreateCharacterController.java**

```java
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
    public String createCharacter(@Validated @ModelAttribute(value="character") CharacterCreateForm characterCreateForm, BindingResult bindingResult, Model model) {

        characterValidation.validateWithModel(characterCreateForm, bindingResult, model);

        if (bindingResult.hasErrors()) {
            return "/createCharacter";
        }

        createCharacterService.saveCharacter(characterCreateForm.toCharacter());

        return "redirect:/";
    }
}

```

HTML Form으로 입력 값을 보내면 `CharacterCreateForm`으로 바인딩되어 검증을 수행한다. 검증 로직은 따로 `CharacterValidation`이라는 클래스를 생성하여 스프링 빈으로 등록하여 사용하였다.



**CharacterValidation.java**

```java
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

```

검증 로직은 최대한 `BindingResult`를 이해하기 위해 Thymeleaf에서 제공하는 기능을 최소한으로 사용하도록 하였다. 따라서 `Model`에 직접 에러 코드와 에러 메시지를 넣어주었다. `BindingResult`에 `addError()` 메서드를 사용하여 `FieldError` 객체를 직접 넣어주는 방법과 `rejectValue()` 메서드를 사용하여 에러 객체를 넣는 방법, 에러 메세지는 `DefaultMessage`를 사용하여 전달하는 방법과 `errors.properties`에 메세지를 직접 작성하여 사용하는 방법까지 해보았다. 

`model` 객체에 직접 메세지를 넣어주는 경우가 있었기 때문에 `validate()` 메서드가 아닌 새로 만든 `validateWithModel()` 메서드를 통해 검증을 수행하도록 하였다.



**errors.properties**

```properties
form.character.email=메일 형식을 지켜주세요
range = 범위는 {0} ~ {1} 사이여야 합니다.
overWeight.character = 키 대비 몸무게가 너무 높아 등록할 수 없습니다.
typeMismatch=숫자를 입력해주세요.
```



**home.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"><head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <button style="flex-direction: column" type="button"
            th:onclick="|location.href='@{/createCharacter}'|">캐릭터 생성 </button>

    <div>
        <h1> 캐릭터 목록 </h1>
        <table border="1">
            <tr>
                <th>이름</th>
                <th>나이</th>
                <th>이메일</th>
                <th>키</th>
                <th>몸무게</th>
            </tr>
            <tr th:each="character : ${characterList}">
                <td th:text="${character.name}">username</td>
                <td th:text="${character.age}">0</td>
                <td th:text="${character.email}"></td>
                <td th:text="${character.height}"></td>
                <td th:text="${character.weight}"></td>
            </tr>
        </table>
    </div>
</body>
</html>
```

타임리프를 통해 `model`에 들어있는 `Character` 객체들을 손쉽게 화면에 표시하였다.



**createCharacter.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>

    <style>
        .field-error {
            border-color: #dc3545;
            color: #dc3545;
        }
    </style>

</head>
<body>
    <form th:action="@{/createCharacter}" method="post">

        <div th:if="${globalError} != null">
            <p class="field-error" th:text="#{overWeight.character}"> </p>
        </div>

        <div>
            <label for="name"> 이름 </label>
            <input type="text" id="name" name="name" th:value="${character.getName()}">
            <div th:if="${nameError} != null" th:text="${nameError}" th:class="field-error"></div>
        </div>
        <div>
            <label for="age"> 나이 </label>
            <input type="text" id="age" name="age" th:value="${character.getAge()}">
            <div th:if="${ageError} != null" th:text="${ageError}" th:class="field-error"></div>
        </div>
        <div>
            <label for="height"> 키 </label>
            <input type="text" id="height" name="height" th:value="${character.getHeight()}">
            <div th:errors="${character.height}" th:class="field-error" ></div>
        </div>

        <div>
            <label for="weight"> 몸무게 </label>
            <input type="text" id="weight" name="weight" th:value="${character.getWeight()}">
            <div th:errors="${character.weight}" th:class="field-error" ></div>
        </div>

        <div>
            <label for="email"> 이메일 </label>
            <input type="text" id="email" name="email" th:value="${character.getEmail()}">
            <div th:if="${emailError} != null" th:text="#{form.character.email}" th:class="field-error"></div>
        </div>


        <div>
            <button type="submit"> 등록 </button>
        </div>
    </form>
</body>
</html>

```

에러 메시지를 직접 `model`에 넣어서 화면에 표시했기 때문에 `th:errors`가 아닌 `th:if`를 통해 에러가 있으면 에러 메시지를 표시하는 방법을 사용하였다. 하지만 `errors.porperties`에서 에러 메세지를 가져오는 경우에는 `BindingResult`에 서 에러 메세지를 꺼내는 방법이 없었기 때문에 `th:errors` 메서드를 사용하였다.



### 결과

1. 이름 입력 값 검증

   - 실패

     ![image](https://user-images.githubusercontent.com/68289543/146325135-9eee9edc-d5e0-4807-8a37-23334fb796af.png)

   - 성공

     ![image](https://user-images.githubusercontent.com/68289543/146325242-52a72cd0-28df-41a5-b4be-b338e1b4ff85.png)

2. 나이 입력 값 검증

   - 실패 

     ![image](https://user-images.githubusercontent.com/68289543/146325559-c9eb581a-9e06-41f0-a0ba-b72b3168d6d1.png)

   - 성공

     ![image](https://user-images.githubusercontent.com/68289543/146328183-6554dc7f-a1e3-4c89-a9bc-236fd617b029.png)

3. 키 입력 값 검증

   - 실패

     ![image](https://user-images.githubusercontent.com/68289543/146325680-95aad56f-b805-43f4-8a96-6b2875a6dfa9.png)

   - 성공

     ![image](https://user-images.githubusercontent.com/68289543/146327415-1277a20b-19df-446a-920a-f5abd8e5031f.png)

4. 몸무게 입력 값 검증

   - 실패1 (범위 이탈)

     ![image](https://user-images.githubusercontent.com/68289543/146325901-2ca12071-d6d3-402f-bd0a-cf2c928f6d0e.png)

   - 실패2 (타입 에러)

     ​	![image](https://user-images.githubusercontent.com/68289543/146327930-70ca423f-e392-45f8-9e2b-3ce5655b3b1e.png)

   - 성공

     ​	![image](https://user-images.githubusercontent.com/68289543/146327821-f382ddbd-15e5-4d63-a764-f317c9c40c59.png)

5. 메일 입력 값 검증

   - 실패

     ![image](https://user-images.githubusercontent.com/68289543/146327736-1bda8845-766b-4b26-a8a7-2e185d629b04.png)

   - 성공

     ![image](https://user-images.githubusercontent.com/68289543/146327664-5fd2ea7e-7fff-4a4c-bf90-a59cce3b217c.png)

6. 글로벌 에러

   - 실패

     ![image](https://user-images.githubusercontent.com/68289543/146326581-2b57fe05-eb7b-4c9e-8c2e-134da81b0ff5.png)

7. 성공

   ![image](https://user-images.githubusercontent.com/68289543/146328293-6d90e0a6-0463-44b6-9a05-504029e516bb.png)

# 2. Bean Validation 사용

Bean Validation을 사용하여 위와 같은 검증을 수행할 수 있도록 한다.

**프로젝트 구성**

- SpringMVC 5.3.13
- ThymeLeaf 2.6.1
- Lombok

```bash
├─main
│  ├─generated
│  ├─java
│  │  └─Kong
│  │      └─validationPractice
│  │          │  ValidationPracticeApplication.java
│  │          │
│  │          ├─character
│  │          │      Character.java
│  │          │      CharacterCreateForm.java
│  │          │
│  │          ├─controller
│  │          │      CreateCharacterController.java
│  │          │      HomeController.java
│  │          │
│  │          ├─repository
│  │          │      CharacterRepository.java
│  │          │
│  │          ├─service
│  │          │      CharacterListService.java
│  │          │      CreateCharacterService.java
│  │          │
│  │          └─validation
│  │                  CharacterValidation.java
│  │
│  └─resources
│      │  application.properties
│      │  errors.properties
│      │
│      ├─static
│      └─templates
│              createCharacterV1.html
│              createCharacterV2.html
│              home.html
│
└─test
    └─java
        └─Kong
            └─validationPractice
                    ValidationPracticeApplicationTests.java
```



### 내용

검증 조건은 위와 같다. 



### 코드

BeanValidation을 `CharacterCreateForm`에 적용하였다.

**CharacterCreateForm**

```java
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

```



**HomeContrller - 추가**

```java
@RequestMapping(value = "/createCharacterV2", method = RequestMethod.GET)
public String createCharacterV2(Model model) {
    model.addAttribute("character", new CharacterCreateForm());
    return "createCharacterV2";
}
```

HomeController에 새로운 버튼을 추가하여 해당 버튼을 통해 BeanValidation을 사용하는 컨트롤러에 폼 데이터를 전송할 수 있도록 하였다.



**CreateCharacterController - 추가**

```java
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
```

바인딩 되는 객체 앞에 `@Validated` 어노테이션을 적용하여 검증 기능을 활성화하였다. 오브젝트 오류는 직접 작성하였다. (`bindingResult.reject()`)



**createCharacterV2.html**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>

    <style>
        .field-error {
            border-color: #dc3545;
            color: #dc3545;
        }
    </style>

</head>
<body>
    <form th:action="@{/createCharacterV2}" method="post" th:object="${character}">

        <div th:if="${#fields.hasGlobalErrors()}">
            <p class="field-error" th:each="err : ${#fields.globalErrors()}" th:text="${err}"> </p>
        </div>

        <div>
            <label for="name"> 이름 </label>
            <input type="text" id="name" name="name" th:value="${character.getName()}">
            <div th:errors="${character.name}" th:errorclass="field-error"></div>
        </div>
        <div>
            <label for="age"> 나이 </label>
            <input type="text" id="age" name="age" th:value="${character.getAge()}">
            <div th:errors="${character.age}" th:errorclass="field-error"></div>
        </div>
        <div>
            <label for="height"> 키 </label>
            <input type="text" id="height" name="height" th:value="${character.getHeight()}">
            <div th:errors="${character.height}" th:errorclass="field-error" ></div>
        </div>

        <div>
            <label for="weight"> 몸무게 </label>
            <input type="text" id="weight" name="weight" th:value="${character.getWeight()}">
            <div th:errors="${character.weight}" th:errorclass="field-error" ></div>
        </div>

        <div>
            <label for="email"> 이메일 </label>
            <input type="text" id="email" name="email" th:value="${character.getEmail()}">
            <div th:errors="${character.email}" th:errorclass="field-error"></div>
        </div>
        
        <div>
            <button type="submit"> 등록 </button>
        </div>
    </form>
</body>
</html>
```

타임리프 에러 처리 코드를 적극 사용하였다.



**Errors.properties - 추가**

```properties
# BeanValidation
Length.name = 이름은 {2}글자 에서 {1}글자 사이어야 합니다.
Range.age = 나이는 {2}세 이상 {1}세 이하여야 합니다.
Range.height = 키는 {2}cm 이상 {1}cm 이하여야 합니다.
Range.weight = 몸무게는 {2}kg 이상 {1}kg 이하여야 합니다.
NotNull=값을 입력해주세요.
Email= 이메일 형식이 아닙니다.
```

어노테이션의 이름을 `code`로 하여 메세지가 생성되기 때문에 직접 바꿀 수 있다.



### 느낀점

백앤드 검증에 사용되는 BindingResult를 처음 접해보았는데 간단하지만 매우 단순한 예시임에도 불구하고 코드가 매우 길어졌고 실제 기능을 구현한 코드보다 검증을 위한 코드가 훨씬 많고 반복적이어서 비효율적이고 유지보수에 힘들겠다고 생각하였다. 그렇기 때문에 더욱 BeanValidation을 사용하면서 어노테이션 기반으로 검증을 하니 직관적이고 반복적인 코드가 사라져 유용하게 느껴졌다.



