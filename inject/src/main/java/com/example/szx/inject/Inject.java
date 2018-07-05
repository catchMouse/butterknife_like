package com.example.szx.inject;

public interface Inject<T> {
    void inject(T host, Object object, Provider provider);
}
