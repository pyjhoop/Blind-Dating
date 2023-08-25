package com.blind.dating;

import com.blind.dating.domain.UserAccount;
import com.blind.dating.dto.user.UserRequestDto;
import com.blind.dating.repository.UserAccountRepository;
import com.blind.dating.security.TokenProvider;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DatingApplicationTests {

	private MockMvc mvc;
	@Autowired
	private UserAccountRepository userAccountRepository;
	@Autowired
	private WebApplicationContext context;
	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;


	@Test
	void test() throws Exception {

		List<String> interests = List.of("자전거","놀기");
		List<Boolean> questions = List.of(true, false);
		String pwd = bCryptPasswordEncoder.encode("userPwd111");
		UserRequestDto dto = UserRequestDto.of("userId111",pwd,"nick111","서울","INFP","M","하이요");
		dto.setInterests(interests);
		dto.setQuestions(questions);
		String accessToken = tokenProvider.create(dto.toEntity());
		String refreshToken = tokenProvider.refreshToken(dto.toEntity());
		UserAccount user = dto.toEntity();
		user.setRecentLogin(LocalDateTime.now());
		user.setRefreshToken(refreshToken);
		user.setDeleted(false);

		UserAccount result = userAccountRepository.save(user);


		mvc.perform(get("/api/hello")
						.header("Authorization", "Bearer "+accessToken))
				.andExpect(status().isOk());
	}
}
