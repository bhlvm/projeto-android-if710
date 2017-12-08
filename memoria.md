# Memória


## Avaliações

Foram realizadas avaliações da memória utilizando o as ferramentas Android Profiler, LeakCanary e procstats.

#### Android Profiler

O uso de memória do aplicativo ficou oscilando sempre entre 10 e 16mb. Sendo algo em torno de 16mb quando em primeiro plano e de 10mb em segundo plano. Segue imagem abaixo:

![Imagem 1](https://raw.githubusercontent.com/msb55/projeto-android-if710/master/imagens_relatorio/memory_AndroidProfiler.png)

Utilizando a aplicação para abrir activities e dar scroll na listView o uso de memória segue constante. 
Rolando a listView não há alteração na quantidade de memória utilizada devido ao uso de RecyclerView, assim ao invés de serem criados novos objetos para os itens visíveis da lista, são aproveitados aqueles criados anteriormente. 


#### LeakCanary

Não foi encontrado nenhum vazamento de memória utilizando essa ferramenta

#### Procstats

os dados abaixos foram obtidos utilizando o comando **adb procstats --hours 3**
que gera um relatório do uso de memória nas últimas 3 horas.

    br.ufpe.cin.if710.podcast / u0a71 / v1:
    TOTAL: 93% (7.6MB-6.5MB-96MB/5.2MB-5.3MB-92MB over 54)
    Top: 92% (7.6MB-6.3MB-96MB/5.2MB-5.2MB-92MB over 53)
    Imp Fg: 0.04% 
    Service: 1.0% (16MB-16MB-16MB/12MB-12MB-12MB over 1) 
    (Last Act): 0.03%
    (Cached): 0.19% 


A tabela mostra que o aplicativo ficou 93% do tempo na RAM, o que é esperado uma vez que a avaliação foi feita utilizando um dispositivo virtual. Apenas 0.19% cached e 0.04% no estado de important foreground. 

## Boas práticas

#### RecyclerView

o RecyclerView identifica quais são as views visíveis ao usuário e quando o usuário da scroll  na lista, o componente identifica as views que não estão mais visíveis para o usuário e as reutiliza. Isso faz com que haja um número fixo de objetos criados destinados à listview, diminuindo o consumo de memória.

Como apresentado no código abaixo, o método getView recebe como parâmetro uma posição referente ao item que será mostrado e uma View converView. A convertView é aquela que será reciclada e caso ela não esteja nula será reutilizada. 


    1 public View getView(int position, View convertView, ViewGroup parent) {
    2    final ViewHolder holder;
    3   if (convertView == null) {
    4        convertView = View.inflate(getContext(), linkResource, null);
    5       holder = new ViewHolder();
    6       holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
    7        holder.item_date = (TextView) convertView.findViewById(R.id.item_date);
    8        holder.btn_Download = (Button) convertView.findViewById(R.id.item_action);
    9       convertView.setTag(holder);
    10    } else {
    11        holder = (ViewHolder) convertView.getTag(); //Recicla uma view
    12    }



