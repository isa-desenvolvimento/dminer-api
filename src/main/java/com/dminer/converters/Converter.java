package com.dminer.converters;

public interface Converter<E, D, R> {
    
    public D entityToDto(E entity);

    public E dtoToEntity(D dto);

    public E dtoRequestToEntity(R requestDto);
}