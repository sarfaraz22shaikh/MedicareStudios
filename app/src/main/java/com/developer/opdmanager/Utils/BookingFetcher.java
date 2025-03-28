package com.developer.opdmanager.Utils;

import android.util.Log;

import com.developer.opdmanager.Models.Bookingrequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingFetcher {
    private static final String TAG = "BookingFetcher";
    private final FirebaseFirestore db;
    private final String doctorId;
    private final BookingFetchListener listener;

    public BookingFetcher(String doctorId, BookingFetchListener listener) {
        this.db = FirebaseFirestore.getInstance();
        this.doctorId = doctorId;
        this.listener = listener;
    }

    public void fetchPendingBookings() {
        List<Bookingrequest> pendingBookings = new ArrayList<>();
        CollectionReference slotsRef = db.collection("doctors").document(doctorId).collection("slots");

        slotsRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Log.e(TAG, "Error fetching slots", task.getException());
                listener.onBookingsFetched(pendingBookings);
                return;
            }

            QuerySnapshot slotDocs = task.getResult();
            if (slotDocs.isEmpty()) {
                Log.d(TAG, "No slots found for doctor: " + doctorId);
                listener.onBookingsFetched(pendingBookings);
                return;
            }

            AtomicInteger completedTasks = new AtomicInteger(0);
            int totalTasks = slotDocs.size();

            for (QueryDocumentSnapshot slotDoc : slotDocs) {
                String slotId = slotDoc.getId();
                CollectionReference bookingRef = slotsRef.document(slotId).collection("bookings");

                Query query = bookingRef.whereEqualTo("status", "pending");
                query.get().addOnCompleteListener(bookingTask -> {
                    if (bookingTask.isSuccessful() && bookingTask.getResult() != null) {
                        AtomicInteger patientFetchTasks = new AtomicInteger(0);
                        int totalPatientTasks = bookingTask.getResult().size();

                        if (totalPatientTasks == 0) {
                            if (completedTasks.incrementAndGet() == totalTasks) {
                                Log.d(TAG, "All pending bookings fetched. Total: " + pendingBookings.size());
                                listener.onBookingsFetched(pendingBookings);
                            }
                            return;
                        }

                        for (QueryDocumentSnapshot bookingDoc : bookingTask.getResult()) {
                            try {
                                Bookingrequest booking = bookingDoc.toObject(Bookingrequest.class);
                                if (booking != null) {
                                    // Set doctorId, slotId, and bookingId
                                    booking.setDoctorId(doctorId);
                                    booking.setSlotId(slotId);
                                    booking.setBookingId(bookingDoc.getId());

                                    String patientId = booking.getPatientId();
                                    if (patientId != null) {
                                        // Fetch patient name
                                        db.collection("Patients").document(patientId)
                                                .get()
                                                .addOnSuccessListener(patientDoc -> {
                                                    if (patientDoc.exists()) {
                                                        String patientName = patientDoc.getString("name");
                                                        booking.setPatientName(patientName != null ? patientName : "Unknown Patient");
                                                    } else {
                                                        booking.setPatientName("Patient Not Found");
                                                    }
                                                    synchronized (pendingBookings) {
                                                        pendingBookings.add(booking);
                                                    }
                                                    if (patientFetchTasks.incrementAndGet() == totalPatientTasks) {
                                                        if (completedTasks.incrementAndGet() == totalTasks) {
                                                            Log.d(TAG, "All pending bookings fetched. Total: " + pendingBookings.size());
                                                            listener.onBookingsFetched(pendingBookings);
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    booking.setPatientName("Error Fetching Patient");
                                                    synchronized (pendingBookings) {
                                                        pendingBookings.add(booking);
                                                    }
                                                    if (patientFetchTasks.incrementAndGet() == totalPatientTasks) {
                                                        if (completedTasks.incrementAndGet() == totalTasks) {
                                                            Log.d(TAG, "All pending bookings fetched. Total: " + pendingBookings.size());
                                                            listener.onBookingsFetched(pendingBookings);
                                                        }
                                                    }
                                                });
                                    } else {
                                        booking.setPatientName("Patient ID Not Available");
                                        synchronized (pendingBookings) {
                                            pendingBookings.add(booking);
                                        }
                                        if (patientFetchTasks.incrementAndGet() == totalPatientTasks) {
                                            if (completedTasks.incrementAndGet() == totalTasks) {
                                                Log.d(TAG, "All pending bookings fetched. Total: " + pendingBookings.size());
                                                listener.onBookingsFetched(pendingBookings);
                                            }
                                        }
                                    }
                                } else {
                                    Log.w(TAG, "Booking is null for document: " + bookingDoc.getId());
                                    if (patientFetchTasks.incrementAndGet() == totalPatientTasks) {
                                        if (completedTasks.incrementAndGet() == totalTasks) {
                                            Log.d(TAG, "All pending bookings fetched. Total: " + pendingBookings.size());
                                            listener.onBookingsFetched(pendingBookings);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting booking document: " + bookingDoc.getId(), e);
                                if (patientFetchTasks.incrementAndGet() == totalPatientTasks) {
                                    if (completedTasks.incrementAndGet() == totalTasks) {
                                        Log.d(TAG, "All pending bookings fetched. Total: " + pendingBookings.size());
                                        listener.onBookingsFetched(pendingBookings);
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "Error fetching bookings for slot: " + slotId, bookingTask.getException());
                        if (completedTasks.incrementAndGet() == totalTasks) {
                            Log.d(TAG, "All pending bookings fetched. Total: " + pendingBookings.size());
                            listener.onBookingsFetched(pendingBookings);
                        }
                    }
                });
            }
        });
    }
    public void fetchApprovedBookings() {
        List<Bookingrequest> approvedBookings = new ArrayList<>();
        CollectionReference slotsRef = db.collection("doctors").document(doctorId).collection("slots");

        slotsRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                Log.e(TAG, "Error fetching slots", task.getException());
                listener.onBookingsFetched(approvedBookings);
                return;
            }

            QuerySnapshot slotDocs = task.getResult();
            if (slotDocs.isEmpty()) {
                Log.d(TAG, "No slots found for doctor: " + doctorId);
                listener.onBookingsFetched(approvedBookings);
                return;
            }

            AtomicInteger completedTasks = new AtomicInteger(0);
            int totalTasks = slotDocs.size();

            for (QueryDocumentSnapshot slotDoc : slotDocs) {
                String slotId = slotDoc.getId();
                CollectionReference bookingRef = slotsRef.document(slotId).collection("bookings");

                Query query = bookingRef.whereEqualTo("status", "approved");
                query.get().addOnCompleteListener(bookingTask -> {
                    if (bookingTask.isSuccessful() && bookingTask.getResult() != null) {
                        AtomicInteger patientFetchTasks = new AtomicInteger(0);
                        int totalPatientTasks = bookingTask.getResult().size();

                        if (totalPatientTasks == 0) {
                            if (completedTasks.incrementAndGet() == totalTasks) {
                                Log.d(TAG, "All approved bookings fetched. Total: " + approvedBookings.size());
                                listener.onBookingsFetched(approvedBookings);
                            }
                            return;
                        }

                        for (QueryDocumentSnapshot bookingDoc : bookingTask.getResult()) {
                            try {
                                Bookingrequest booking = bookingDoc.toObject(Bookingrequest.class);
                                if (booking != null) {
                                    booking.setDoctorId(doctorId);
                                    booking.setSlotId(slotId);
                                    booking.setBookingId(bookingDoc.getId());

                                    String patientId = booking.getPatientId();
                                    if (patientId != null) {
                                        db.collection("Patients").document(patientId)
                                                .get()
                                                .addOnSuccessListener(patientDoc -> {
                                                    if (patientDoc.exists()) {
                                                        String patientName = patientDoc.getString("name");
                                                        booking.setPatientName(patientName != null ? patientName : "Unknown Patient");
                                                    } else {
                                                        booking.setPatientName("Patient Not Found");
                                                    }
                                                    synchronized (approvedBookings) {
                                                        approvedBookings.add(booking);
                                                    }
                                                    if (patientFetchTasks.incrementAndGet() == totalPatientTasks) {
                                                        if (completedTasks.incrementAndGet() == totalTasks) {
                                                            Log.d(TAG, "All approved bookings fetched. Total: " + approvedBookings.size());
                                                            listener.onBookingsFetched(approvedBookings);
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    booking.setPatientName("Error Fetching Patient");
                                                    synchronized (approvedBookings) {
                                                        approvedBookings.add(booking);
                                                    }
                                                    if (patientFetchTasks.incrementAndGet() == totalPatientTasks) {
                                                        if (completedTasks.incrementAndGet() == totalTasks) {
                                                            Log.d(TAG, "All approved bookings fetched. Total: " + approvedBookings.size());
                                                            listener.onBookingsFetched(approvedBookings);
                                                        }
                                                    }
                                                });
                                    } else {
                                        booking.setPatientName("Patient ID Not Available");
                                        synchronized (approvedBookings) {
                                            approvedBookings.add(booking);
                                        }
                                        if (patientFetchTasks.incrementAndGet() == totalPatientTasks) {
                                            if (completedTasks.incrementAndGet() == totalTasks) {
                                                Log.d(TAG, "All approved bookings fetched. Total: " + approvedBookings.size());
                                                listener.onBookingsFetched(approvedBookings);
                                            }
                                        }
                                    }
                                } else {
                                    Log.w(TAG, "Booking is null for document: " + bookingDoc.getId());
                                    if (patientFetchTasks.incrementAndGet() == totalPatientTasks) {
                                        if (completedTasks.incrementAndGet() == totalTasks) {
                                            Log.d(TAG, "All approved bookings fetched. Total: " + approvedBookings.size());
                                            listener.onBookingsFetched(approvedBookings);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error converting booking document: " + bookingDoc.getId(), e);
                                if (patientFetchTasks.incrementAndGet() == totalPatientTasks) {
                                    if (completedTasks.incrementAndGet() == totalTasks) {
                                        Log.d(TAG, "All approved bookings fetched. Total: " + approvedBookings.size());
                                        listener.onBookingsFetched(approvedBookings);
                                    }
                                }
                            }
                        }
                    } else {
                        Log.e(TAG, "Error fetching bookings for slot: " + slotId, bookingTask.getException());
                        if (completedTasks.incrementAndGet() == totalTasks) {
                            Log.d(TAG, "All approved bookings fetched. Total: " + approvedBookings.size());
                            listener.onBookingsFetched(approvedBookings);
                        }
                    }
                });
            }
        });
    }

    public interface BookingFetchListener {
        void onBookingsFetched(List<Bookingrequest> bookings);
    }
}