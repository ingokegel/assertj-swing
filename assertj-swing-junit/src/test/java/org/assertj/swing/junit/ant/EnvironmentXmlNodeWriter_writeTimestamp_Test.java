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
package org.assertj.swing.junit.ant;

import static org.apache.tools.ant.taskdefs.optional.junit.XMLConstants.TIMESTAMP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.assertj.swing.junit.xml.XmlAttribute;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

/**
 * Test for <code>{@link EnvironmentXmlNodeWriter#writeTimestamp(org.assertj.swing.junit.xml.XmlNode)}</code>.
 *
 * @author Alex Ruiz
 */
public class EnvironmentXmlNodeWriter_writeTimestamp_Test extends EnvironmentXmlNodeWriter_TestCase {

  private Date date;
  private String formatted;

  @Override
  void onSetUp() {
    date = new Date();
    formatted = "2009-06-13T15:06:10";
  }

  @Test
  public void shouldWriteFormattedCurrentDateAsAttribute() {
    when(timeStampFormatter.format(argThat(new BeforeOrEqualDateMatcher(date)))).thenReturn(formatted);
    assertThat(writer.writeTimestamp(targetNode)).isSameAs(writer);
    verify(targetNode).addAttribute(XmlAttribute.name(TIMESTAMP).value(formatted));
  }

  private static class BeforeOrEqualDateMatcher implements ArgumentMatcher<Date> {
    private final Date date;

    BeforeOrEqualDateMatcher(Date date) {
      this.date = date;
    }

    @Override
    public boolean matches(Date other) {
      if (other == null)
        return false;
      return date.before(other) || date.equals(other);
    }

    @Override
    public String toString() {
      return String.valueOf(date);
    }
  }
}
