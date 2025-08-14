package vn.urbansteps.security;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * PasswordEncoder that accepts existing plain-text (no prefix) passwords while
 * delegating to Spring's DelegatingPasswordEncoder for encoded values.
 *
 * - If encoded password starts with "{...}", delegate as usual.
 * - If not, fall back to NoOp comparison so legacy records still work.
 * - encode() always uses the delegate (bcrypt by default).
 */
public class LenientPasswordEncoder implements PasswordEncoder {
    private final PasswordEncoder delegate = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final PasswordEncoder noop = new PasswordEncoder() {
        @Override public String encode(CharSequence rawPassword) {
            return rawPassword == null ? "" : rawPassword.toString();
        }
        @Override public boolean matches(CharSequence rawPassword, String encodedPassword) {
            String raw = rawPassword == null ? "" : rawPassword.toString();
            return encodedPassword != null && encodedPassword.equals(raw);
        }
    };

    @Override
    public String encode(CharSequence rawPassword) {
        return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) return false;
        if (encodedPassword.startsWith("{")) {
            return delegate.matches(rawPassword, encodedPassword);
        }
        // Legacy DB value without id prefix -> treat as noop
        return noop.matches(rawPassword, encodedPassword);
    }
}
