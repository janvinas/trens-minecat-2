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

## Comandaments

- `/tm info` Proporciona informació sobre el tren que un jugador està editant.
- `/tm departures <estació>` Retorna les properes sortides d'una estació.
- `/tm getdisplay <tipus> <estació>` Genera una pantalla d'un tipus determinat per una estació.
- `/tm vdisplay <estacíó> <prefix> <andana> <periode>` Genera una pantalla de text (variables signlink).
- `/tm vdisplays` Llista totes les vdisplays.
