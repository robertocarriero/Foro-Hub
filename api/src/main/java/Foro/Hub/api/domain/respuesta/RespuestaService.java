package Foro.Hub.api.domain.respuesta;


import Foro.Hub.api.domain.topico.TopicoRepository;
import Foro.Hub.api.domain.usuario.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;


@Component
public class RespuestaService implements CommandLineRunner {


    @Autowired
    private RespuestaRepository respuestaRepository;

    @Autowired
    private TopicoRepository topicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
    }
}


