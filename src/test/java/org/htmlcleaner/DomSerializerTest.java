/*  Copyright (c) 2006-2013, the HtmlCleaner Project
    All rights reserved.

    Redistribution and use of this software in source and binary forms,
    with or without modification, are permitted provided that the following
    conditions are met:

    * Redistributions of source code must retain the above
      copyright notice, this list of conditions and the
      following disclaimer.

    * Redistributions in binary form must reproduce the above
      copyright notice, this list of conditions and the
      following disclaimer in the documentation and/or other
      materials provided with the distribution.

    * The name of HtmlCleaner may not be used to endorse or promote
      products derived from this software without specific prior
      written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
    LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
    CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
    SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
    INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
    CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
    ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
    POSSIBILITY OF SUCH DAMAGE.
*/

package org.htmlcleaner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.input.DOMBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;

public class DomSerializerTest extends AbstractHtmlCleanerTest {
	
	@Test
	public void errorChecking() throws ParserConfigurationException{
		TagNode node = cleaner.clean("<p>");
    	DomSerializer ser = new DomSerializer(cleaner.getProperties(), true, true, false);
    	Document document = ser.createDocument(node);
    	assertFalse(document.getStrictErrorChecking());
	}
    
	/**
	 * See issue 108
	 * @throws IOException
	 */
    @Test
    @Ignore
    public void html5doctype() throws Exception{
    	cleaner.getProperties().setUseCdataForScriptAndStyle(true);
    	cleaner.getProperties().setOmitCdataOutsideScriptAndStyle(true);
    	String initial = readFile("src/test/resources/test23.html");
    	TagNode tagNode = cleaner.clean(initial);
    	DomSerializer ser = new DomSerializer(cleaner.getProperties());
    	Document dom = ser.createDOM(tagNode);
    	assertNotNull(dom.getChildNodes().item(0).getChildNodes().item(0));
    	assertEquals("head", dom.getChildNodes().item(0).getChildNodes().item(0).getNodeName());
    }
    
	/**
	 * See issue 127
	 * @throws IOException
	 */
    @Test
    public void rootNodeAttributes() throws Exception{
    	cleaner.getProperties().setUseCdataForScriptAndStyle(true);
    	cleaner.getProperties().setOmitCdataOutsideScriptAndStyle(true);
    	String initial = readFile("src/test/resources/test29.html");
    	TagNode tagNode = cleaner.clean(initial);
    	DomSerializer ser = new DomSerializer(cleaner.getProperties());
    	Document dom = ser.createDOM(tagNode);
    	assertNotNull(dom.getChildNodes().item(0).getChildNodes().item(0));
    	assertEquals("http://unknown.namespace.com", dom.getChildNodes().item(0).getAttributes().getNamedItem("xmlns").getNodeValue());
    	assertEquals("27", dom.getChildNodes().item(0).getAttributes().getNamedItem("id").getNodeValue());
    	//
    	// Check we have a real ID attribute in the DOM and not just a regular attribute
    	//
    	assertEquals("http://unknown.namespace.com", dom.getElementById("27").getAttribute("xmlns"));
    }
    
    @Test
    public void cdata() throws Exception{
    	cleaner.getProperties().setUseCdataForScriptAndStyle(true);
    	cleaner.getProperties().setOmitCdataOutsideScriptAndStyle(true);
    	String initial = "<script> this &gt; that </script>";
    	TagNode tagNode = cleaner.clean(initial);
    	DomSerializer ser = new DomSerializer(cleaner.getProperties(), cleaner.getProperties().isAdvancedXmlEscape(), true);
    	Document dom = ser.createDOM(tagNode);
    	DOMBuilder in = new DOMBuilder();
    	org.jdom2.Document jdomDoc = in.build(dom);
		XMLOutputter outputter = new XMLOutputter(Format.getRawFormat().setEncoding("UTF-8").setLineSeparator("\n"));
		String actual = outputter.outputString(jdomDoc);
        Assert.assertTrue(actual.contains("this > that"));
    }
    
    @Test
    public void cdata2() throws Exception{
    	cleaner.getProperties().setUseCdataForScriptAndStyle(true);
    	cleaner.getProperties().setOmitCdataOutsideScriptAndStyle(true);
    	String initial = "<script> this &gt; that </script>";
    	TagNode tagNode = cleaner.clean(initial);
    	DomSerializer ser = new DomSerializer(cleaner.getProperties(), cleaner.getProperties().isAdvancedXmlEscape(), false);
    	Document dom = ser.createDOM(tagNode);
    	DOMBuilder in = new DOMBuilder();
    	org.jdom2.Document jdomDoc = in.build(dom);
		XMLOutputter outputter = new XMLOutputter(Format.getRawFormat().setEncoding("UTF-8").setLineSeparator("\n"));
		String actual = outputter.outputString(jdomDoc);
        Assert.assertTrue(actual.contains("this &gt; that"));
    }

  
	
}
