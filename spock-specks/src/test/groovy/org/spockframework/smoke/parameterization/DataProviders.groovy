/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.spockframework.smoke.parameterization

import org.junit.runner.RunWith
import spock.lang.*
import org.spockframework.runtime.SpeckExecutionException
import static spock.lang.Predef.*
import static org.spockframework.smoke.EmbeddedSpeckRunner.*

/**
 * @author Peter Niederwieser
 */
@Speck
@RunWith(Sputnik)
class DataProviders {
  def "null value"() {
    when:
    runFeatureBody """
expect: true
where: x << null
    """

    then:
    thrown(SpeckExecutionException)
  }

  def "literal"() {
    expect: x == 1
    where : x << 1
  }

  def "empty list"() {
    when:
    runFeatureBody """
expect: true
where : x << []
    """

    then:
    thrown(SpeckExecutionException)
  }

  def "single-element list"() {
    expect: x == 1
    where : x << [1]
  }

  def "multi-element list"() {
    expect: [1, 2, 3].contains x
    where : x << [3, 2, 1]
  }

  def "array"() {
    expect: [1, 2, 3].contains x
    where : x << ([3, 2, 1] as int[])
  }

  def "map"() {
    expect: x.value == x.key.toUpperCase()
    where : x << ["vienna":"VIENNA", "tokyo":"TOKYO"]
  }

  def "matcher"() {
    expect: x.startsWith "a"
    where : x << ("a1a2a3" =~ /a./)
  }

  def "string"() {
    expect: "abc".contains x
    where : x << "cba"
  }

  def "iterator"() {
    expect: [1, 2, 3].contains x
    where : x << new MyIterator()
  }

  def "iterable"() {
    expect: [1, 2, 3].contains x
    where : x << new MyIterable()
  }

  def "disguised iterable"() {
    expect: [1, 2, 3].contains x
    where : x << new MyDisguisedIterable()
  }

  def "enumeration"() {
    expect: ["a", "b", "c"].contains x
    where : x << new StringTokenizer("b c a")
  }

  def "data providers with same number of elements"() {
    expect: x == y
    where : x << [1, 2, 3]; y << [1, 2, 3]
  }

  def "data providers with different number of elements"() {
    expect: x == y
    where : x << [1, 2, 3]; y << [1]
  }

  def "data providers one of which has no data"() {
    when:
    runFeatureBody """
expect: true
where:
x << (1..3)
y << []
    """

    then:
    thrown(SpeckExecutionException)
  }

  def "different kinds of data providers used together"() {
    expect: a + b + c + d + e == "abcde"
    where :
      a << "a"
      b << ["b"]
      c << ("c" =~ /./)
      d << (["d"] as String[])
      e << "YES".toLowerCase()[1]
  }

  def "computed"() {
    expect: a == b
    where:
    a << {
      def x = 0
      def y = 1
      [x, y]
    }()
    b << [0, 1]
  }
}

private class MyIterator implements Iterator {
  def elems = [1, 2, 3]

  boolean hasNext() {
    elems.size() > 0
  }

  Object next() {
    elems.pop()
  }

  void remove() {}
}

private class MyIterable implements Iterable {
  Iterator iterator() {
    [3, 2, 1].iterator()
  }
}

// doesn't implement Iterable
private class MyDisguisedIterable {
  Iterator iterator() {
    [3, 2, 1].iterator()
  }
}