/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.adminfaces.starter.service;

import java.util.List;

import com.github.adminfaces.starter.model.Usuario;

public interface UsuarioService extends CrudService<Usuario, Long> {

	public List<Usuario> findByNomeLike(String nome);
}