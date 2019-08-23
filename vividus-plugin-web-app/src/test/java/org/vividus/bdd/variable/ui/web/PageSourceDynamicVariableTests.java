package org.vividus.bdd.variable.ui.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.vividus.selenium.IWebDriverProvider;

@ExtendWith(MockitoExtension.class)
class PageSourceDynamicVariableTests
{

    @Mock
    private IWebDriverProvider webDriverProvider;

    @InjectMocks
    private PageSourceDynamicVariable pageSourceDynamicVariable;

    @Test
    void shouldReturnPageSource()
    {
        String sources = "<html></html>";
        WebDriver driver = mock(WebDriver.class);
        when(webDriverProvider.get()).thenReturn(driver);
        when(driver.getPageSource()).thenReturn(sources);

        assertEquals(sources, pageSourceDynamicVariable.getValue());
    }

}
