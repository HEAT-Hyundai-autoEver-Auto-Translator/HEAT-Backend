//package com.hyundaiautoever.HEAT.v1.util;
//
//import com.hyundaiautoever.HEAT.v1.entity.Language;
//import com.hyundaiautoever.HEAT.v1.entity.User;
//import com.hyundaiautoever.HEAT.v1.repository.LanguageRepository;
//import com.hyundaiautoever.HEAT.v1.repository.user.UserRepository;
//import com.hyundaiautoever.HEAT.v1.service.LanguageService;
//import com.hyundaiautoever.HEAT.v1.service.UserService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class TestDataInit {
//
//    private final UserRepository userRepository;
//    private final LanguageRepository languageRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @PostConstruct
//    public void init() {
//
//        //언어 정보 초기화
//        Language korean = new Language();
//        korean.setLanguageNo(1);
//        korean.setLanguageName("Korean");
//        korean.setLanguageCode("ko");
//        languageRepository.save(korean);
//
//        Language english = new Language();
//        english.setLanguageNo(2);
//        english.setLanguageName("English");
//        english.setLanguageCode("en");
//        languageRepository.save(english);
//
//        Language chinese = new Language();
//        chinese.setLanguageNo(3);
//        chinese.setLanguageName("Chinese");
//        chinese.setLanguageCode("zh-CN");
//        languageRepository.save(chinese);
//
//        Language spanish = new Language();
//        spanish.setLanguageNo(4);
//        spanish.setLanguageName("Spanish");
//        spanish.setLanguageCode("es");
//        languageRepository.save(spanish);
//
//        Language portuguese = new Language();
//        portuguese.setLanguageNo(5);
//        portuguese.setLanguageName("Portuguese");
//        portuguese.setLanguageCode("pt");
//        languageRepository.save(portuguese);
//
//        Language german = new Language();
//        german.setLanguageNo(6);
//        german.setLanguageName("German");
//        german.setLanguageCode("de");
//        languageRepository.save(german);
//
//        Language czech = new Language();
//        czech.setLanguageNo(7);
//        czech.setLanguageName("Czech");
//        czech.setLanguageCode("cs");
//        languageRepository.save(czech);
//
//        Language slovak = new Language();
//        slovak.setLanguageNo(8);
//        slovak.setLanguageName("Slovak");
//        slovak.setLanguageCode("sk");
//        languageRepository.save(slovak);
//
//        Language russian = new Language();
//        russian.setLanguageNo(9);
//        russian.setLanguageName("Russian");
//        russian.setLanguageCode("ru");
//        languageRepository.save(russian);
//
//        Language hindi = new Language();
//        hindi.setLanguageNo(10);
//        hindi.setLanguageName("Hindi");
//        hindi.setLanguageCode("hi");
//        languageRepository.save(hindi);
//
//        Language indonesian = new Language();
//        indonesian.setLanguageNo(11);
//        indonesian.setLanguageName("Indonesian");
//        indonesian.setLanguageCode("id");
//        languageRepository.save(indonesian);
//
//        Language arabic = new Language();
//        arabic.setLanguageNo(12);
//        arabic.setLanguageName("Arabic");
//        arabic.setLanguageCode("ar");
//        languageRepository.save(arabic);
//
//        Language vietnamese = new Language();
//        vietnamese.setLanguageNo(13);
//        vietnamese.setLanguageName("Vietnamese");
//        vietnamese.setLanguageCode("vi");
//        languageRepository.save(vietnamese);
//
//
//        // 테스트 유저 생성
//        LocalDate now = LocalDate.now();
//
//        User user1 = new User();
//        user1.setLastAccessDate(now);
//        user1.setPasswordHash(passwordEncoder.encode("password1"));
//        user1.setRefreshToken("refresh1");
//        user1.setSignupDate(now);
//        user1.setUserEmail("test1@example.com");
//        user1.setUserName("Test User 1");
//        user1.setUserRole(UserRole.user);
//        user1.setLanguage(languageRepository.getOne(1)); // Korean
//        userRepository.save(user1);
//
//        User user2 = new User();
//        user2.setLastAccessDate(now);
//        user2.setPasswordHash(passwordEncoder.encode("password2"));
//        user2.setRefreshToken("refresh2");
//        user2.setSignupDate(now);
//        user2.setUserEmail("test2@example.com");
//        user2.setUserName("Test User 2");
//        user2.setUserRole(UserRole.admin);
//        user2.setLanguage(languageRepository.getOne(2)); // English
//        userRepository.save(user2);
//
//        User user3 = new User();
//        user3.setLastAccessDate(now);
//        user3.setPasswordHash(passwordEncoder.encode("password3"));
//        user3.setRefreshToken("refresh3");
//        user3.setSignupDate(now);
//        user3.setUserEmail("test3@example.com");
//        user3.setUserName("Test User 3");
//        user3.setUserRole(UserRole.user);
//        user3.setLanguage(languageRepository.getOne(3)); // Chinese
//        userRepository.save(user3);
//
//        User user4 = new User();
//        user4.setLastAccessDate(now);
//        user4.setPasswordHash(passwordEncoder.encode("password4"));
//        user4.setRefreshToken("refresh4");
//        user4.setSignupDate(now);
//        user4.setUserEmail("test4@example.com");
//        user4.setUserName("Test User 4");
//        user4.setUserRole(UserRole.admin);
//        user4.setLanguage(languageRepository.getOne(4)); // Spanish
//        userRepository.save(user4);
//
//        User user5 = new User();
//        user5.setLastAccessDate(now);
//        user5.setPasswordHash(passwordEncoder.encode("password5"));
//        user5.setRefreshToken("refresh5");
//        user5.setSignupDate(now);
//        user5.setUserEmail("test5@example.com");
//        user5.setUserName("Test User 5");
//        user5.setUserRole(UserRole.user);
//        user5.setLanguage(languageRepository.getOne(5)); // Portuguese
//        userRepository.save(user5);
//
//    }
//
//}
