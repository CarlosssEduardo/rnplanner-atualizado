
// Endereço da sua classe dentro do projeto - Ele organiza o seu código e evita conflito de nomes.
package br.com.rnplanner;

// Import: São ferramentas que o Java possui, Biblioteca do Spring Boot.
/*🔹 SpringApplication
Essa classe é responsável por:
✅ Iniciar o servidor
✅ Criar o contexto do Spring
✅ Carregar configurações
✅ Subir o Tomcat embutido
👉 Ela é basicamente o botão LIGAR O SISTEMA. */
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
/*🔹 @SpringBootApplication
Essa anotação é MUITO importante ⚠️
Ela é tipo um “superpoder” que ativa várias coisas ao mesmo tempo.
Ela é a junção de 3 anotações:
@Configuration → diz que essa classe pode ter configurações
@EnableAutoConfiguration → Spring configura tudo automaticamente
@ComponentScan → procura classes anotadas como:
@Controller
@Service
@Repository
@Component */
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RnplannerApplication {

	public static void main(String[] args) {
		/*Aqui acontece a mágica 🪄
O que essa linha faz?
Cria o contexto do Spring (IoC Container)
Lê as configurações
Procura beans
Inicializa dependências
Sobe o servidor embutido (Tomcat)
Deixa a sua API rodando */
		SpringApplication.run(RnplannerApplication.class, args);
	}
	@Bean
	CommandLineRunner init() {
		return args -> {
			System.out.println("Sistema iniciado com sucesso!");
		};
	}


}
