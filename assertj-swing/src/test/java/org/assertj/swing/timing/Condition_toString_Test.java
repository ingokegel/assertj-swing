/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2018 the original author or authors.
 */
package org.assertj.swing.timing;

import org.junit.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Strings.concat;

public class Condition_toString_Test {
  @Test
  public void should_Return_Description_Text() {
    Supplier<String> description = () -> "Hello World!";
    MyCondition condition = new MyCondition(description);
    assertThat(condition.toString()).isEqualTo("Hello World!");
  }

  @Test
  public void should_Return_Description_Text_Set_As_String() {
    MyCondition condition = new MyCondition("Hello World!");
    assertThat(condition.toString()).isEqualTo("Hello World!");
  }

  @Test
  public void should_Return_Condition_Type_If_Description_Is_Null() {
    MyCondition condition = new MyCondition((Supplier<String>) null);
    assertThat(condition.toString()).isEqualTo(concat("condition of type [", MyCondition.class.getName(), "]"));
  }

  @Test
  public void should_Append_Addendum() {
    MyCondition condition = new MyCondition("Hello World!") {
      @Override
      protected String descriptionAddendum() {
        return " append this";
      }
    };
    assertThat(condition.toString()).isEqualTo("Hello World! append this");
  }

  private static class MyCondition extends Condition {
    MyCondition(String description) {
      super(description);
    }

    MyCondition(Supplier<String> description) {
      super(description);
    }

    @Override
    public boolean test() {
      return false;
    }
  }
}
