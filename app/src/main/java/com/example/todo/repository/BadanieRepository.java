package com.example.todo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.todo.model.Badanie;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BadanieRepository {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private final MutableLiveData<List<Badanie>> allBadania = new MutableLiveData<>(new ArrayList<>());
    private ListenerRegistration listener;

    public BadanieRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        startListeningForBadania();
    }

    /** Nasłuchuje zmian w users/{uid}/badania i aktualizuje LiveData */
    private void startListeningForBadania() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            allBadania.setValue(new ArrayList<>());
            return;
        }
        String uid = user.getUid();

        if (listener != null) listener.remove();

        listener = db.collection("users")
                .document(uid)
                .collection("badania")
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null || snapshot == null) return;

                    List<Badanie> list = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Badanie b = mapDocToBadanie(doc);
                        if (b != null) list.add(b);
                    }
                    allBadania.setValue(list);
                });
    }

    private Badanie mapDocToBadanie(DocumentSnapshot doc) {
        if (!doc.exists()) return null;

        Badanie b = new Badanie();
        // pole do przechowywania ID dokumentu w Firestore
        b.setFirestoreId(doc.getId());

        Map<String, Object> data = doc.getData();
        if (data == null) return b;

        Object nazwa = data.get("nazwa");
        Date dataOstatnia = doc.getDate("dataOstatniegoBadania");
        Object okres = data.get("okresWaznosciDni");
        Object notatki = data.get("notatki");

        if (nazwa instanceof String) b.setNazwa((String) nazwa);
        if (notatki instanceof String) b.setNotatki((String) notatki);

        // Dostosuj typ daty do tego, jak ją zapisujesz (String / long):
        if (dataOstatnia != null) {
            b.setDataOstatniegoBadania(dataOstatnia);
        }
        if (okres instanceof Long) {
            b.setOkresWaznosciDni(((Long) okres).intValue());
        }

        return b;
    }

    public LiveData<List<Badanie>> getAllBadania() {
        return allBadania;
    }

    /** Dodanie nowego badania */
    public void insertBadanie(Badanie badanie) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        // jeśli badanie już ma firestoreId -> update; jak nie -> nowy dokument
        String docId = badanie.getFirestoreId();
        if (docId == null || docId.isEmpty()) {
            docId = db.collection("users")
                    .document(uid)
                    .collection("badania")
                    .document().getId();
            badanie.setFirestoreId(docId);
        }

        db.collection("users")
                .document(uid)
                .collection("badania")
                .document(docId)
                .set(mapBadanieToMap(badanie));
    }

    public void updateBadanie(Badanie badanie) {
        insertBadanie(badanie); // set() nadpisze dokument
    }

    public void deleteBadanie(Badanie badanie) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        if (badanie.getFirestoreId() == null || badanie.getFirestoreId().isEmpty()) return;

        db.collection("users")
                .document(user.getUid())
                .collection("badania")
                .document(badanie.getFirestoreId())
                .delete();
    }

    private Map<String, Object> mapBadanieToMap(Badanie b) {
        Map<String, Object> map = new HashMap<>();
        map.put("nazwa", b.getNazwa());
        // tu zakładam, że getter zwraca Date:
        map.put("dataOstatniegoBadania", b.getDataOstatniegoBadania());
        map.put("okresWaznosciDni", b.getOkresWaznosciDni());
        map.put("notatki", b.getNotatki());
        map.put("updatedAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
        return map;
    }


    public void clear() {
        if (listener != null) listener.remove();
    }
}
