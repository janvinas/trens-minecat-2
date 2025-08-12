# Trens Minecat v2

Versió millorada del plugin de gestió de trens pel servidor MineCat.

## Dependències d'execució

Abans d'instal·lar aquest plugin, cal tenir les següents dependències al servidor:

- [BKCommonLib](https://www.spigotmc.org/resources/bkcommonlib.39590/)
- [TrainCarts](https://www.spigotmc.org/resources/traincarts.39592/)
- [SignLink](https://www.spigotmc.org/resources/signlink.39593/)

## Funcions

- Generació de trens amb horaris definits per expressions cron.
- Càlcul de l'hora estimada de pas i el retard dels trens quan s'aturen a una estació.
- Presentació de les dades en cartells de text o images en mapes.

## Configuració

Al directori `services` podem crear arxius XML per definir els serveis (línies). Els arxius poden tenir qualsevol nom.

Exemple d'arxiu de servei:

```xml
<service>
    <destination>Estació 5</destination>
    <line>R7</line>
    <time>2,5,8,11,14,17,20,23,26,29,32,35,38,41,44,47,50,53,56,59 * * * *</time>
    <world>world</world>
    <x>-487</x>
    <y>67</y>
    <z>-4570</z>
    <heading>north</heading>
    <train>R7</train>
    <stationList>
      <station platform="3" departuretime="97">Estació 1</station>
      <station platform="1" departuretime="173">Estació 2</station>
      <station platform="1" departuretime="229">Estació 3</station>
      <station platform="1" departuretime="301">Estació 4</station>
      <station platform="2" departuretime="490" newdest="Estació 1">Estació 5</station>
      <station platform="3" departuretime="591">Estació 4</station>
      <station platform="2" departuretime="666">Estació 3</station>
      <station platform="2" departuretime="724">Estació 2</station>
    </stationList>
</service>
```
L'etiqueta `<time>` conté una expressió cron que indica quan ha d'aparèixer un tren a la posició definida. Aquesta ha de ser les coordenades d'un block de rail.

L'etiqueta `<train>` és el nom del tren de TrainCarts. Pot ser un patró (per exemple mmm genera tres minecarts) o un tren desat.
## Comandaments

- `/tm info` Proporciona informació sobre el tren que un jugador està editant.
- `/tm departures <estació>` Retorna les properes sortides d'una estació.
- `/tm getdisplay <model> <estació>` Genera una pantalla d'un model determinat per una estació.
- `/tm vdisplay <estacíó> <prefix> <andana> <periode>` Genera una pantalla de text (variables signlink).
- `/tm vdisplays` Llista totes les vdisplays.

## Pantalles de text

Podem generar una pantalla que mostra la propera sortida d'una andana en un cartell de Minecraft normal. S'utilitzen variables de SignLink que s'actualitzaran periòdicament. Podem generar una pantalla d'aquest tipus amb el comandament:

`/tm vdisplay <estacíó> <prefix> <andana> <període>`

- `<estació>` Nom de l'estació. Si conté espais, els hem de substituir per `_`.
- `<prefix>` Prefix de les variables. Cal que sigui curt perquè hi càpiga als cartells. 
- `<andana>` Andana per la qual volem mostrar la següent sortida.
- `<període>` Temps entre actualitzacions de les variables el ticks (unitats de 0.05 segons). Convé no fer-lo massa curt perquè el càlcul de la propera sortida pot ser computacionalment car si tenim molts serveis.

Un cop executat el comandament, es crearan diverses variables que podem posar en un cartell:

- `<prefix>_n` Nom del tren
- `<prefix>_l` Línia del servei
- `<prefix>_d` Destinació del tren
- `<prefix>_ld` Línea i destinació del tren, separats per un espai
- `<prefix>_t` Hora de sortida del tren
- `<prefix>_i` Mostra si el tren va a l'hora o està endarrerit

Per posar les variables a un cartell cal posar el nom de la variable amb el caràcter `%` abans i després.

## Pantalles d'imatge

Aquestes pantalles es mostren sobre un mapa, que podem col·locar en una paret amb un item frame. Si volem posar el mateix cartell a diversos llocs, és millor agafar-lo amb l'acció "Pick block" (sol ser el botó central del ratolí) i posar el mateix mapa en un altre frame, en comptes de crear pantalles duplicades.

Actualment, només hi ha disponible el model "1", però en el futur s'afegiran més.