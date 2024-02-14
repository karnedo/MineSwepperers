# MineSweepperers

MineSweepperers se basa en el clásico buscaminas pero añadiendo la posibilidad de jugar con tus amigos! MineSweepperers permite que puedas jugar al buscaminas online en un sistema basado por turnos, donde los jugadores que toquen una bomba pierden y los que no, ganan.
MineSweepperers está diseño para jugar en tableros desde tan solo 2x2 baldosas hasta las nxn. De igual forma, internamente permite hasta n>0 jugadores. Sin embargo, tanto la cantidad de baldosas como el número de jugadores están limitados por motivos de jugabildiad. 

### Modelo cliente-servidor
El programa trabaja bajo el modelo cliente-servidor. Es decir, existe un programa que hará de central a otros programas cliente, los cuales se comunicarán únicamente con el servidor

#### Cliente
Para ejecutar el cliente use `java -jar client.jar`. 
En esta pantalla, especifique el servidor y el nombre que tendrás en el juego:

![Pantalla del cliente](https://i.imgur.com/Jil0hDh.png)

Espera a que el resto de jugadores se conecte. Una vez dentro, cuando sea su turno, presione donde crea que no hay una bomba:

![Juego](https://i.imgur.com/BS2sEIj.png)

#### Servidor
Para ejecutar el servidor use `java -jar server.jar <tamaño del tablero> <número de jugadores>`. 
Si por el contrario desea ejecutar el servidor con una interfaz, puede hacer directamente `java -jar server.jar`.

Si ejecuta el servidor por terminal, este se ocupará de hacer el matchmaking automática. En cambio, si ejecuta el servidor
utilizando la interfaz, deberá de especificar el tamaño del tablero y el número de jugadores:

![Servidor](https://i.imgur.com/g7L0shR.png)
