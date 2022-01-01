package prague.util;

import com.google.common.base.Converter;

import javax.annotation.Nonnull;

/**
 * convert int array to string
 *
 * Created by ch.wang on 21 Feb 2014.
 */
public class ArrayStringConverter extends Converter<int[], String> {

  private final IntCharConverter icc = new IntCharConverter();

  @Override
  protected String doForward(@Nonnull int[] array) {
    StringBuilder sb = new StringBuilder(array.length);
    for (int item : array) {
      sb.append(icc.convert(item));
    }
    return sb.toString();
  }

  @Override
  protected int[] doBackward(@Nonnull String s) {
    int n = s.length();
    int[] array = new int[n];
    for (int i = 0; i < n; i++) {
      char c = s.charAt(i);
      array[i] = icc.reverse().convert(c);
    }
    return array;
  }
}

class IntCharConverter extends Converter<Integer, Character> {

  private static final int RADIX = 36;

  @Override
  protected Character doForward(@Nonnull Integer i) {
    char c = Character.forDigit(i, RADIX);
    return c;
  }

  @Override
  protected Integer doBackward(@Nonnull Character c) {
    int digit = Character.digit(c, RADIX);
    if (digit == -1) {
      throw new RuntimeException("Character " + c + " convert failed");
    }
    return digit;
  }
}