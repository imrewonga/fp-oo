;;; Exercise 1

(load-file "sources/scheduling.clj")

;; First, I converted the `registrants-courses` (which I never really liked anyway) into
;; a `registrant` map:

{:manager? true
 :course-names ["zig" "zag"]}

;; It seems useful to have all the reasons why a course might be unavailable
;; in one place, so I added another clause to `note-unavailability`. Notice that
;; I passed only whether or not the registrant is a manager or not, because that's
;; all that's relevant to this function.


(def note-unavailability
     (fn [courses instructor-count manager?]
       (let [out-of-instructors?
             (= instructor-count
                (count (filter (fn [course] (not (:empty? course)))
                               courses)))]
         (map (fn [course]
                (assoc course
                       :unavailable? (or (:full? course)
                                         (and out-of-instructors?
                                              (:empty? course))
                                         (and manager?                     ;; <<===
                                              (not (:morning? course))))))
              courses))))

;;; All else that's required is to call `note-unavailability` and `answer-annotations` correctly:

(def annotate
     (fn [courses registrant instructor-count]
       (-> courses
           (answer-annotations (:course-names registrant))
           domain-annotations
           (note-unavailability instructor-count (:manager? registrant)))))

(def half-day-solution
     (fn [courses registrant instructor-count]
       (-> courses
           (annotate registrant instructor-count)
           visible-courses
           ((fn [courses] (sort-by :course-name courses)))
           final-shape)))

(def solution
     (fn [courses registrant instructor-count]
       (map (fn [courses]
              (half-day-solution courses registrant instructor-count))
            (separate :morning? courses))))
