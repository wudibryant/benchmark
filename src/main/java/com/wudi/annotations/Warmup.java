package com.wudi.annotations;
//预热
public @interface Warmup {
    int iterations() default 0;
}
