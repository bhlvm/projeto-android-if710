# Architecture Components

## ROOM
Para a migração do banco de dados para o Room, a classe ```ItemFeed``` foi modificada e foram adicionadas 3 novas classes: ```ItemFeedDAO```, ```ItemFeedDatabase```, ```ItemFeedListViewModel```.

### ItemFeed

Foram adicionadas tags para que o Room faça o mapeamento da mesma em uma tabela. Foi definido que o link de download seria a chave primária, dado que este é único e não nulo.
```@Entity``` define a classe como uma entidade (tabela)
```@ColunInfo``` define o atributo como uma das colunas da tabela.

```java 
@Entity(tableName = "itens")
public class ItemFeed {
   
    @PrimaryKey @NonNull
    @ColumnInfo(name = "downloadLink")
    private final String downloadLink;
    @ColumnInfo(name = "status")
    private String status;
    ...
```

### ItemFeedDAO

Essa classe representa o DAO (Data Access Object), responsável pelas operações básicas no banco de dados, ela possui somente métodos abstratos que serão gerenciados pelo Room. Cada método recebe uma marcação, para sinalizar qual operação será feita no banco.  
```@Dao``` sinaliza que ao Room que esta é uma classe DAO
```@Query ```realiza uma operação de pesquisa
```@Insert``` realiza uma operação de inserção
```@Update``` realiza uma operação de update
```@delete``` realiza uma operação de delete

Outro ponto importante é que o método getItens() retorna um objeto ```LiveData```.
```java 
@Dao
public interface  ItemFeedDAO {

    @Query("SELECT * FROM itens")
    public LiveData<List<ItemFeed>> getItens();
    @Insert(onConflict = IGNORE)
    public abstract void insertItem(ItemFeed item);
    @Update()
    public abstract void update(ItemFeed item);
    @Query("SELECT * FROM itens WHERE downloadLink = :link")
    public abstract ItemFeed getByDownloadLink(String link);
    @Delete()
    public abstract void delete(ItemFeed item);

}
```
### ItemFeedDatabase

Classe responsável por criar o banco de dados e fazer o acesso direto ao DAO. É subclasse de RoomDatabase e, dessa forma, implementa o getDatabase que recebe um contexto e retorna uma instancia do banco de dados.
```@Database``` indica que essa classe representa o banco de dados.
```entities = {ItemFeed.class}``` define as classes que pertencentes ao banco.
```Room.databaseBuilder``` recebe um contexto, a classe que representa o banco e o nome do banco.
```java
@Database(entities = {ItemFeed.class}, version = 1)
public abstract class ItemFeedDatabase extends RoomDatabase {
    private static ItemFeedDatabase INSTANCE;
    public abstract ItemFeedDAO itemDao();

    public static ItemFeedDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), ItemFeedDatabase.class, "podcast.db")
                            .build();
        }
        return INSTANCE;
    }
}
```

### ItemFeedListViewModel

Extende de ```AndroidViewModel``` e é responsavel por armazenar e gerenciar dados relacionados à interface do usuário, no nosso caso, ela será responsável por fazer as chamadas ao banco de dados.
No seu construtor ela recebe uma aplication, inicializa o banco de dados e preeche a variavel ```itemFeedList``` com os dados do banco, essa é List que irá alimentar o ```XMLFeedAdapter```. Vale lembrar que ```itemDatabase.itemDao().getItens();``` retorna um objeto do tipo  ```LiveData<List<ItemFeed>>```. métodos ```deleteItem```, ```insertItem``` e ```updateItem``` fazem suas operações de forma em ```AsyncTask``.
```java
public ItemFeedListViewModel(@NonNull Application application) {
        super(application);
        itemDatabase = ItemFeedDatabase.getDatabase(this.getApplication());
        itemFeedList = itemDatabase.itemDao().getItens();
    }
    
    public LiveData<List<ItemFeed>> getItemFeedList() {
        return itemFeedList;
    }
    public ItemFeed getByDownloadLink(String link) {return                     itemDatabase.itemDao().getByDownloadLink(link);}
    public void deleteItem(ItemFeed itemFeed) {
        new deleteAsyncTask(itemDatabase).execute(itemFeed);
    }
    public void insertItem(ItemFeed itemFeed) {
        new insertAsyncTask(itemDatabase).execute(itemFeed);
    }
    public void updateItem(ItemFeed itemFeed) {
        new updateAsyncTask(itemDatabase).execute(itemFeed);
    }
    ...
```

O objeto ItemFeedListViewModel é criado no OnCreate da ```MainActivity```.
```java
MyApplication.viewModel = ViewModelProviders.of(this).get(ItemFeedListViewModel.class);
```
e acessado sempre que necessário dessa forma: ``` MyApplication.viewModel.insertItem(item);```

## LiveData

Como dito anteriormente, o método ```getItens()``` da classe ```ItemFeedDAO``` retorna um objeto do tipo ```LiveData<List<ItemFeed>>```. Definimos um observador para o objeto, assim, sempre que  uma mudança ocorre no mesmo, a ```ListView``` da ```MainActivity``` é atualizada.
```java
 MyApplication.viewModel.getItemFeedList().observe(this, new Observer<List<ItemFeed>>() {
            @Override
            public void onChanged(@Nullable List<ItemFeed> itens) {
                adapter.addItens(itens);
                listViewItens.setAdapter(adapter);
            }
        });
``` 

