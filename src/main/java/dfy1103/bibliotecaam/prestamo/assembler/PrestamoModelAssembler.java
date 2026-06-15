package dfy1103.bibliotecaam.prestamo.assembler;
import dfy1103.bibliotecaam.prestamo.controller.PrestamoController;
import dfy1103.bibliotecaam.prestamo.dto.PrestamoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class PrestamoModelAssembler implements RepresentationModelAssembler<PrestamoResponseDTO, EntityModel<PrestamoResponseDTO>> {

    @Override
    public EntityModel<PrestamoResponseDTO> toModel(PrestamoResponseDTO prestamoDto){
        return EntityModel.of(prestamoDto,
                linkTo(methodOn(PrestamoController.class).obtenerPorId(prestamoDto.getIdPresta())).withSelfRel(),
                linkTo(methodOn(PrestamoController.class).obtenerTodos()).withRel("prestamos"));
    }
}
