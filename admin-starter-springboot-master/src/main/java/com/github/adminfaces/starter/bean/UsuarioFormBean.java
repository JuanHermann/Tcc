package com.github.adminfaces.starter.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component // substitui @ManagedBean
@Scope("request") // substitui @RequestScope
public class UsuarioFormBean {
}
