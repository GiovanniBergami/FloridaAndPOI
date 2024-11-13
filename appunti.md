Large Scale project
Query utili con mongodb:
fare il doc linking:

for (const myCity of db.cities.find()) {
// Trova tutti i punti di interesse relativi alla città corrente
const Punti = db.POIs.find({ city: myCity.nome }).toArray(); // Converte il cursor in un array

    // Estrai solo gli `_id` dei punti di interesse
    const POI_ids = Punti.map(punto => punto._id);

    // Aggiungi l'array di riferimenti `_id` alla città
    db.cities.updateOne(
        { _id: myCity._id },
        { $set: { "POI_ids": POI_ids } }
    );
}

fare una aggregazione su una variabile:

db.cities.aggregate(
{
$group : { _id: null , sommaq : { $sum : "$POI_count"}}
})

for ( const myUser of db.users.find()){ 
    for ( const f of myUser.friends){
        db.users.updateOne(
            {user_id = f.user_id},
            {$addToSet: {friends : myUser.user_id}}
        )

    }
}

per sistemare gli user id nella lista degli amici sarebbe utile farsi un bell'indice sugli user id, cosi facendo un doppio for sugli
user si velocizza di molto
oppure si fa una nuova collection con solo _id e user_id con indice su user_id
a quel punto si scorrono i documenti di partenza e in ogni array di amici si traducono i valori grazie alla nuova collection,
facendo accesso veloce ad user_id e sostituendolo col corrispondente object id. in effetti si può usare anche la collection stessa

for user in users
    prendi la lista degli amici
    sostituisci ciascuno con l'object id (necessaria ricerca sulla collection stessa, ma velocizzata dall'indice)

 
for(const user of db.users.find()){
const friendIds = [];
for (const friendUserId of user.friends){
const friend = db.users.findOne({user_id : friendUserId});
if(friend){
friendIds.push(friend._id);
}
}
db.users.updateOne(
{_id:user._id},
{$set:{friends : friendIds}}
);
}
    


rimuovere duplicati
const names = db.users.aggregate([
{
$group: {
_id: "$name",         // Raggruppa per il campo "name"
count: { $sum: 1 }    // Conta il numero di occorrenze di ogni nome
}
},
{
$match: {
count: { $gt: 1 }     // Filtra solo i gruppi con più di un'istanza (duplicati)
}
},
{
$group: {
_id: null,                 // Usa `_id: null` per aggregare tutto in un unico documento
duplicates: { $push: "$_id" }  // Colleziona i nomi duplicati in un array `duplicates`
}
},
{
$project: {
_id: 0,                // Esclude l'ID dal risultato
duplicates: 1          // Include solo l'array dei duplicati
}
}
]).toArray();

// Ora dobbiamo rimuovere solo la prima occorrenza di ogni duplicato
names.forEach(doc => {
doc.duplicates.forEach(name => {
// Rimuovi solo la prima occorrenza di ciascun nome
db.users.deleteOne({ name: name });
});
});


vedere duplicati

db.users.aggregate([
{
$group: {
_id: "$name",         // Raggruppa per il campo "name"
count: { $sum: 1 }    // Conta il numero di occorrenze di ogni nome
}
},
{
$match: {
count: { $gt: 1 }     // Filtra solo i gruppi con più di un'istanza (duplicati)
}
},
{
$group: {
_id: null,                 // Usando `_id: null` aggrega tutto in un unico documento
duplicates: { $push: "$_id" }  // Colleziona i nomi duplicati in un array `duplicates`
}
},
{
$project: {
_id: 0,                // Esclude l'ID dal risultato
duplicates: 1          // Include solo l'array dei duplicati
}
}
]).toArray();

INSERISCI CUMULATA DELLE STELLE NEI POI

for (const poi of db.POIs.find()) {
// Verifica che reviews_ids sia un array
if (Array.isArray(poi.reviews_ids) && poi.reviews_ids.length > 0) {
let sumStars = 0;

        // Itera su ciascun ID di recensione e recupera la recensione per sommare le stelle
        for (const reviewId of poi.reviews_ids) {
            const review = db.reviews.findOne({ _id: reviewId });
            if (review && review.stars) {
                sumStars += review.stars;
            }
        }

        // Aggiorna il POI con la somma delle stelle
        db.POIs.updateOne(
            { _id: poi._id },
            { $set: { sumStars: sumStars } }
        );
    } else {
        // Se reviews_ids non è un array o è vuoto, imposta sumStars a 0
        db.POIs.updateOne(
            { _id: poi._id },
            { $set: { sumStars: 0 } }
        );
    }
}
