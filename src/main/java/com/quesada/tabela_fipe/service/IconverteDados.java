package com.quesada.tabela_fipe.service;

public interface IconverteDados {
    <T> T obeterDados(String json, Class<T> classes);
}
