package co.com.udea.tauri.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.com.udea.tauri.entities.Entrada;

@Repository
public interface EntradaRepository extends JpaRepository<Entrada, Integer>{

}
