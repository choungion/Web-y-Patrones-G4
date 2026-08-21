package com.taru.service;

import com.taru.domain.Rol;
import com.taru.domain.Usuario;
import com.taru.repository.RolRepository;
import com.taru.repository.UsuarioRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            FirebaseStorageService firebaseStorageService,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.firebaseStorageService = firebaseStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean soloActivos) {
        if (soloActivos) {
            return usuarioRepository.findByActivoTrue();
        }
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Integer> getIdsEstudiantesConCuenta(Integer idUsuarioAExcluir) {
        return usuarioRepository.findByEstudianteIsNotNull().stream()
                .filter(u -> idUsuarioAExcluir == null || !u.getIdUsuario().equals(idUsuarioAExcluir))
                .map(u -> u.getEstudiante().getIdEstudiante())
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional
    public void save(Usuario usuario, MultipartFile imagenFile) {
        final Integer idUsuario = usuario.getIdUsuario();
        Optional<Usuario> duplicado = usuarioRepository.findByUsernameOrCorreo(usuario.getUsername(), usuario.getCorreo());
        if (duplicado.isPresent()) {
            Usuario encontrado = duplicado.get();
            if (idUsuario == null || !encontrado.getIdUsuario().equals(idUsuario)) {
                throw new DataIntegrityViolationException("El usuario o correo ya esta en uso.");
            }
        }

        if (usuario.getEstudiante() != null && usuario.getEstudiante().getIdEstudiante() != null) {
            Optional<Usuario> yaAsociado = usuarioRepository
                    .findByEstudiante_IdEstudiante(usuario.getEstudiante().getIdEstudiante());
            if (yaAsociado.isPresent()) {
                Usuario encontrado = yaAsociado.get();
                if (idUsuario == null || !encontrado.getIdUsuario().equals(idUsuario)) {
                    throw new DataIntegrityViolationException(
                            "Ese estudiante ya tiene una cuenta asociada (" + encontrado.getUsername() + ").");
                }
            }
        }

        boolean asignarRolPorDefecto = false;
        if (idUsuario == null) {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                throw new IllegalArgumentException("La contraseña es obligatoria para nuevos usuarios.");
            }
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            asignarRolPorDefecto = true;
        } else {
            if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
                Usuario usuarioExistente = usuarioRepository.findById(idUsuario)
                        .orElseThrow(() -> new IllegalArgumentException("Usuario a modificar no encontrado."));
                usuario.setPassword(usuarioExistente.getPassword());
                usuario.setRoles(usuarioExistente.getRoles());
            } else {
                usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
        }

        usuario = usuarioRepository.save(usuario);

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                String rutaImagen = firebaseStorageService.uploadImage(imagenFile, "usuario", usuario.getIdUsuario());
                usuario.setRutaImagen(rutaImagen);
                usuarioRepository.save(usuario);
            } catch (IOException e) {
            }
        }

        if (asignarRolPorDefecto) {
            asignarRolPorUsername(usuario.getUsername(), "ESTUDIANTE");
        }
    }

    @Transactional
    public void delete(Integer idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("El usuario con ID " + idUsuario + " no existe.");
        }
        try {
            usuarioRepository.deleteById(idUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el usuario. Tiene datos asociados.", e);
        }
    }

    @Transactional
    public Usuario asignarRolPorUsername(String username, String nombreRol) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        Rol rol = rolRepository.findByRol(nombreRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));
        usuario.getRoles().add(rol);
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<String> getRolesNombres() {
        return rolRepository.findAll().stream().map(Rol::getRol).toList();
    }

    @Transactional
    public Usuario eliminarRol(String username, Integer idRol) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        usuario.getRoles().removeIf(rol -> rol.getIdRol().equals(idRol));
        return usuarioRepository.save(usuario);
    }
}