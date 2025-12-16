package com.platform.common.model.view;

public class Views {
    public interface Summary {}

    public interface Public extends Summary {}

    public interface Internal extends Public {}

    public interface Audit extends Internal {}
}