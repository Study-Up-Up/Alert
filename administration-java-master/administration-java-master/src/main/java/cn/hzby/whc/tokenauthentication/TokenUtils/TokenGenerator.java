package cn.hzby.whc.tokenauthentication.TokenUtils;

import org.springframework.stereotype.Component;

@Component
public interface TokenGenerator {

    public String generate(String... strings);

}
