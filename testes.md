# Conjunto de Testes

Foram realizados dois testes ao total :
 - PodcastProvider.class
 - MainActivity.class

### PodcastProvider.class

Para o teste de unidade, no cenário do aplicativo de Podcast, ficamos sem muitas alternativas, uma vez que os possíveis candidatos mapeados eram a classe modelo ```ItemFeed.class```  e o provider ```PodcastProvider.class```, ambos com métodos possíveis de serem testados. 

Para o ItemFeed os testes ficariam muito vagos, envolvendo apenas a criação de objetos, inserção em estruturas de dados (lista), alteração de campos e comparações. Basicamente exercendo apenas os métodos getter e setter da classe.

Mas para o PodcastProvider haveriam testes de métodos mais interessantes, envolvendo o banco de dados. Porem para implementar testes nessa classe, algumas dependencias externas se fizeram necessárias, uma vez que tem toda uma regra de negócio para acesso e persistencia no banco.

A proposta inicial do teste com a classe PodcastProvider era ser um teste de unidade, porém devido alguns problemas de erros do tipo: "Method ... not mocked" e do contexto do teste ser um provider, foi necessário migrar o teste para um de integração, fazendo uso do Mockito, JUnit e ProviderTestCase2.

```java
@RunWith(AndroidJUnit4.class)
public class PodcastProviderTest extends ProviderTestCase2<PodcastProvider>{

    private MockContentResolver mMockResolver;

    public PodcastProviderTest() {
        super(PodcastProvider.class, "br.ufpe.cin.if710.podcast.feed");
    }
```

A tag ```@RunWith(AndroidJUnit4.class)``` notifica que se trata de uma classe Android JUnit 4. A classe de teste é filha de ProviderTestCase2 para ser possível a criação de uma ambiente isolado.

 > This base class extends AndroidTestCase, so it provides the JUnit testing framework as well as Android-specific methods for testing application permissions. The most important feature of this class is its initialization, which creates the isolated test environment.

Com um ambiente isolado, as operações de arquivos e base de dados são permitidas, excluindo outras operações com o sistema. O Mockito entra como um _resolver_ para as operações de BD.

> This is a normal ContentProvider object, but it takes all of its environment information from the IsolatedContext, so it is restricted to working in the isolated test environment. All of the tests done in the test case class run against this isolated object.

```java
@Before
public void setUp() throws Exception {
    super.setUp();

    ContentValues cv = new ContentValues();
    cv.put(PodcastDBHelper.EPISODE_TITLE, "Podcast setUp");
    cv.put(PodcastDBHelper.EPISODE_LINK, "Podcast setUp");
    cv.put(PodcastDBHelper.EPISODE_DATE, "05/12/2017");
    cv.put(PodcastDBHelper.EPISODE_DESC, "Descrição do Podcast 1");
    cv.put(PodcastDBHelper.EPISODE_DOWNLOAD_LINK, "www.link_do_podcast_setUp.com.br");
    cv.put(PodcastDBHelper.EPISODE_FILE_URI,"");

    mMockResolver = getMockContentResolver();
    mMockResolver.insert(PodcastProviderContract.EPISODE_LIST_URI,cv);
}

@After
public void tearDown() throws Exception {
    super.tearDown();

    mMockResolver = getMockContentResolver();
    mMockResolver.delete(PodcastProviderContract.EPISODE_LIST_URI, "1", null);
}
```

Definimos para o início de cada teste a inserção de um ItemFeed genérico na tabela do banco, e no final de cada teste a remoção de todos os registros da tabela. Os três testes implementados segue abaixo com breves comentários:

```java
@Test
public void insert() throws Exception {
    ContentValues cv = new ContentValues();
    cv.put(PodcastDBHelper.EPISODE_TITLE, "Podcast 1");
    cv.put(PodcastDBHelper.EPISODE_LINK, "Podcast 1");
    cv.put(PodcastDBHelper.EPISODE_DATE, "05/12/2017");
    cv.put(PodcastDBHelper.EPISODE_DESC, "Descrição do Podcast 1");
    cv.put(PodcastDBHelper.EPISODE_DOWNLOAD_LINK, "www.link_do_podcast_1.com.br");
    cv.put(PodcastDBHelper.EPISODE_FILE_URI,"");

    mMockResolver = getMockContentResolver();
    Uri retorno = mMockResolver.insert(PodcastProviderContract.EPISODE_LIST_URI,cv);

    assertNotNull(retorno);
}
```

O teste de inserção no banco tem o comportamento similar do método ```@Before```, mudando apenas nos dados a serem inseridos e a verificação de retorno, indicado que uma Uri foi retornada com sucesso.

```java
@Test
public void delete() throws Exception {
    String where = PodcastDBHelper.EPISODE_DOWNLOAD_LINK + " LIKE ?";
    String[] whereArgs = new String[] { "www.link_do_podcast_setUp.com.br" };

    mMockResolver = getMockContentResolver();
    int rows = mMockResolver.delete(PodcastProviderContract.EPISODE_LIST_URI, where, whereArgs);

    Assert.assertEquals(1, rows);
}
```

O teste de remoção foi feito para remover o objeto ItemFeed adicionado em ```@Before```, informando uma coluna e seu respectivo valor, e verificando quantas linhas da tabela foram afetadas.

```java
@Test
public void query() throws Exception {
    String[] projection = new String[] { PodcastDBHelper.EPISODE_DOWNLOAD_LINK };

    mMockResolver = getMockContentResolver();
    Cursor cursor = mMockResolver.query(
            PodcastProviderContract.EPISODE_LIST_URI,
            projection,
            null,
            null,
            null
            );

    int count = cursor.getCount();
    assertEquals(1, count);
}
``` 

O teste de busca foi realizado montando uma query simples para todas a coluna _EPISODE_DOWNLOAD_LINK_, o qual é esperado apenas um ItemFeed inserido em ```@Before```

Testes de integração para _ContentProviders_ são realizados da mesma forma que um teste instrumentado, mesma localização e a ambiente (device).

### MainActivity.class
Para o teste de integração escolhemos a tela principal do aplicativo como a classe de teste. Utilizamos tambem o JUnit4 e o Espresso.

```java
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule(MainActivity.class, true);
```

```@RunWith(AndroidJUnit4.class)``` tambem informado que se trata de uma classe Android JUnit 4. Definimos uma ```@Rule``` para a MainActivity, possibilitando um teste funcional para essa _Activity_.

```java
@Test
public void countInitPodcasts() {
    onView(withId(R.id.items)).check(new AdapterCountAssertion(311));
}
@Test
public void keyEvents() {
    onView(withId(R.id.items)).perform(
            pressKey(KeyEvent.KEYCODE_DPAD_DOWN),
            pressKey(KeyEvent.KEYCODE_DPAD_DOWN)
    ).check(new ListSelectionAssertion(1));
}
```

Os dois testes mais simples incluem apenas uma contagem de elementos da ```ListView``` para verificar a quantidade de ItemFeed a partir de um RSSFeed padrão. Esse teste pode vir a falhar futuramente a medida que forem sendo adicionados novos episódios ao feed, uma vez que está parametrizado estáticamente para 311 elementos. O segundo teste simula a interação do usuário na tela, realizando dois movimentos consecutivos para baixo e verificando que o elemento ao topo é o segundo da lista (_index 1_)

```java
@Test
public void notEmptyTitleEpisodeDetail() {
    onData(anything())
            .inAdapterView(withId(R.id.items))
            .atPosition(0)
            .perform(click());

    onView(withId(R.id.titleED)).check(matches(not(withText(""))));
}
```

O teste acima verifica que ao clicar em um item da ```ListView```, redirecionando para a tela de detalhes do ItemFeed, encontrará seu título informado no mesmo. Para esse teste o método ```onData()``` se tornou necessário pois o alvo se encontra dentro de uma ```AdapterView```. E em ```onView()``` a necessidade de analisar a interface, buscando o componente com seu título, e o verificando.

```java
@Test
public void playPodcast() {
    clickButton(0);

    onData(anything())
            .inAdapterView(withId(R.id.items))
            .atPosition(0)
            .onChildView(withId(R.id.item_action))
            .check(matches(withText("pause")));
}

@Test
public void pausePodcast() {
    clickButton(0);
    clickButton(0);

    onData(anything())
            .inAdapterView(withId(R.id.items))
            .atPosition(0)
            .onChildView(withId(R.id.item_action))
            .check(matches(withText("unPause")));
}
```
Nesse dois outros testes é verificado a mudança do label do botão utilizado para tocar o episódio do Podcast, utilizando o método auxiliar ```clickButton()``` parametrizado pela posição do item na ```ListView```, que realiza a ação de clique no botão alvo e posteriormente em cada teste é verificado seu objetivo, play e pause, respectivamente.

Por fim, o método ```clickButton()``` mencionado acima.
```java
public static void clickButton(int position) {
    onData(anything())
            .inAdapterView(withId(R.id.items))
            .atPosition(position)
            .onChildView(withId(R.id.item_action))
            .perform(click());
}
```