
<jsp:include page="header.jsp">
  <jsp:param name="title" value="JSword - Design Overview" />
</jsp:include>

<h1>Design Overview</h1>

<p>This is an overview of JSword that explains how the Book and Bible 
interfaces are arranged and explains some of the design decisions. Note 
that most of what is documented here is current for JSword - there are 
a few sections that reflect where we will be given a bit more re-factoring.</p>

<p>The most important thing any Bible program does is to access Bible data, and in 
more general terms, book data. There may be many different sources of 
data - Bibles stored in different formats, or even on remote systems, 
dictionaries, lexicons and so on. It would be good (where appropriate) 
to be able to treat them all similarly without needing to reinvent the 
wheel too often. Clearly we should be able to use inheritance to specialize 
where needed. So we start by creating an interface that is common to 
all 'Books' but that allows is to do as much as possible. The first 
goal is to read data and to be able to direct where to read from with 
some sort of pointer system. So we start with an interface that looks 
like this:

<pre>interface Book
{
    Data getData(Pointers ptr);
}</pre>

Before we look at what Data and Pointers look like, a Book will need to 
be able to do 2 other things:<br>
Firstly tell us about itself: what it is called, where it comes from and 
so on - MetaData about the Book itself. Secondly help us to find stuff 
by searching. Now searching can be complex and we should not aim to implement 
a full search system in every Book, however I think we can build a couple 
of simple methods that will allow us to construct a powerful generalized 
search system separately. All we need is to find any given word and to 
be able to find words that match a given specification. So we can develop 
our interface like this:

<pre>interface Book
{
    BookMetaData getMetaData();
    Data getData(Pointers ptr);
    Pointers search(String word);
    String[] matchingWords(String spec);
}</pre>

The definition of the matchingWord spec needs attention - in JSword the 
method is called getStartsWith(String base) which allows stemming (the 
most commmon search word manipulation) but does not allow more complex 
wildcard cases. I want to avoid adding too much complexity that will be 
hard to implement and rarely used. Certainly any solution that involves 
regular expression is going to be 1. very hard to implement and 2. not 
actually useful for 99% of users. So I propose a simplifiaction:

<pre>interface Book
{
    ...
    String[] startsWith(String spec);
}</pre>

This interface can be implemented several times, once by something that 
reads Sword format data, once by something that reads JSword format data, 
and so on. The users, and indeed the developers of the front ends do not 
want to know about the various different implementors and what implementations 
need to be looked at. So we can use a couple of classes to fix this. If 
you like GoF patterns, an AbstractFactory:

<pre>interface Books
{
    Iterator listBooks();
    void registerDriver(BookDriver driver);
}

interface BookDriver
{
    Iterator listBooks();
}</pre>

Both listBooks() methods allow you to iterate over the Books known to 
the whole system (Books) or the particular BookDriver. The question is 
what sort of Object should these iterators iterate over?<br>
The Books themselves would be a bad idea because creating a Book may be 
a time and memory consuming process (indexes to be loaded etc) so we need 
some sort of a key to refer to Books by. A simple string is OK, but better 
(and more unique) would be the MetaData objects previously noted. So these 
MetaData objects need to be able to give access to the Book they represent.

<pre>interface Books
{
    Iterator listBooks();
    void registerDriver(BookDriver driver);
    Book getBook(BookMetaData id);
}</pre>

The BookMetaData itself looks something like this: 

<pre>interface BookMetaData
{
    String getName();
}</pre>

Before we move on to what BookData and Pointers look like, I have intentionally 
ignored 2 issues: Encrypted works - some works will need to be encrypted 
- however the finding of keys or deobfustication will be done within the 
Driver so we don't need to worry about it too much. Configuration - some 
Books will need configuring before they will work, maybe with encryption 
keys, maybe with directories under which to find information. Each BookDriver 
will need to take care of configuring the Books that it creates we don't 
attempt to do anything more fancy even though there are parts of JSword 
that have implemented a generic configuration system.

<p>BookData and Pointers are related. BookData describes the actual Book 
text (for example "In the beginning God created ...") and Pointers describe 
where that text comes from (for example "Gen 1:1")

<p>BookData first. We do not want to force users of this code to use it 
in any specific way, so BookData should describe the text in as much 
detail as possible without forcing how that text is used. The final 
display could be a PDA, a web browser, a matching verse list or a full-blow 
GUI display. This to me rules out RTF, HTML and plain text, as they 
are all either display specific (RTF/HTML) or low detail (text), and 
makes me think that XML along with some standard converters to turn 
XML into RTF/HTML/PDF/text/blah is the best choice.<br>
This has the added benefit that it allows us to specify not just what 
output format is required, but also how that transformation is done, 
the fonts and layout details of the produced RTF/HTML are all very configurable.<br>
It also turn out to be very easy in Java simply by using the XSL libraries 
produced by Sun and Apache. XSL libraries are freely available in all 
good languages. :-)<br>
However there is still the question of how to marshal the XML into objects 
for manipulation. In Java there are many options - SAX/DOM/JDOM/JAXB 
- ordered in my opinion from worst for this job (SAX) to best for the 
job (JAXB) however JAXB is still very alpha so I am currently using 
JDOM and I've implemented some classes to hide the marshalling method 
for all but the most fancy of Books.

<p>Pointers are used to request BookData from a Book and are also used as a
reply from a search - so we have Pointers that tell us where the word
"aaron" exists in a particular Book. For the case of a Bible a pointer could
look like this: "Gen 1:1, Isa 45:2, Rev 20:4", for the case of a dictionary
a pointer would be like this: "aaron", or for a BookDriver that contained
sermons: "page 153, para 4-5".<br>
I have not placed a requirement on Pointers for them to apply only to 
single results, or even for the results to be contiguous (Pointers would 
be of little use as search answers if this was to be the case).<br>
The only common feature of Pointers that I can think of is that they 
ought to be convertible to and from strings.

<p>For the Bible case a Pointers is a collection of verses, and JSword 
has a set of classes called Verse, VerseRange and Passage which are 
a fundamental building block. Passage is a specialization of Pointers 
where the Book in question is a Pointer. The Passage package has classes 
to do all sorts of useful manipulations to lists of verses. 

<jsp:include page="footer.jsp" />