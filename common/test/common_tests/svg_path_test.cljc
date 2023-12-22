;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/.
;;
;; Copyright (c) KALEIDOS INC

(ns common-tests.svg-path-test
  (:require
   [app.common.data :as d]
   [app.common.pprint :as pp]
   [app.common.math :as mth]
   [app.common.svg.path :as svg.path]
   [app.common.svg.path.legacy :as svg.path.legacy]
   [clojure.test :as t]
   #?(:cljs [common-tests.arc-to-bezier :as impl])))

(t/deftest parse-test-1
  (let [data (str "m -994.563 4564.1423 149.3086 -52.8821 30.1828 "
                  "-1.9265 5.2446 -117.5157 98.6828 -43.7312 219.9492 "
                  "9.5361 9.0977 121.0797 115.0586 12.7148 -1.1774 "
                  "75.7109 134.7524 3.1787 -6.1008 85.0544 -137.3211 "
                  "59.9137 -301.293 -1.0595 -51.375 25.7186 -261.0492 -7.706 ")
        result1 (->> (svg.path/parse data)
                     (mapv (fn [entry]
                             (update entry :params #(into (sorted-map) %)))))
        result2 (->> (svg.path.legacy/parse data)
                     (mapv (fn [entry]
                            (update entry :params #(into (sorted-map) %)))))]

    (t/is (= 15
             (count result1)
             (count result2)))

    (dotimes [i (count result1)]
      (let [item1 (nth result1 i)
            item2 (nth result2 i)]

        (t/is (= (:command item1)
                 (:command item2)))
        (t/is (= (:params item1)
                 (:params item2)))

        #_(println "------------------------")
        #_(pp/pprint (dissoc item1 :relative))
        #_(pp/pprint (dissoc item2 :prev-pos :relative))))))


(t/deftest parse-test-2
  (let [data (str "M259.958 89.134c-6.88-.354-10.484-1.241-12.44-3.064-1.871-1.743-6.937-3.098-15.793-4.226-7.171-.913"
                  "-17.179-2.279-22.24-3.034-5.06-.755-15.252-2.016-22.648-2.8-18.685-1.985-35.63-4.223-38.572-5.096"
                  "-3.655-1.084-3.016-3.548.708-2.726 1.751.387 13.376 1.701 25.833 2.922 12.456 1.22 29.018 3.114 36.803 "
                  "4.208 29.94 4.206 29.433 4.204 34.267.136 3.787-3.186 5.669-3.669 14.303-3.669 14.338 0 17.18 1.681 "
                  "12.182 7.205-2.053 2.268-1.994 2.719.707 5.42 3.828 3.827 3.74 5.846-.238 5.5-1.752-.153-7.544-.502-12.872"
                  "-.776zm7.563-3.194c0-.778-1.751-1.352-3.892-1.274l-3.893.141 3.539 1.133c1.946.624 3.698 1.197 3.893 1.275"
                  ".194.077.354-.496.354-1.275zm-15.899-8.493c1.43-2.29 1.414-2.83-.084-2.83-2.05 0-5.25 2.76-5.25 4.529 0 "
                  "2.226 3.599 1.08 5.334-1.699zm8.114 0c2.486-2.746 2.473-2.83-.438-2.83-1.65 0-3.683 1.273-4.516 2.83-1.175 "
                  "2.196-1.077 2.831.438 2.831 1.075 0 3.107-1.274 4.516-2.83zm7.814.674c2.858-3.444.476-4.085-3.033-.816"
                  "-2.451 2.284-2.677 2.973-.975 2.973 1.22 0 3.023-.97 4.008-2.157zm-49.571-4.509c-1.168-.43-3.294-1.802-4.725"
                  "-3.051-2.112-1.843-9.304-2.595-38.219-3.994-46.474-2.25-63-4.077-60.27-6.665.324-.308 9.507.261 20.406 "
                  "1.264 10.9 1.003 31.16 2.258 45.024 2.789l25.207.964 4.625-3.527c4.313-3.29 5.41-3.474 16.24-2.732 6.389"
                  ".438 11.981 1.388 12.428 2.111.447.723-.517 2.73-2.141 4.46l-2.954 3.144c1.607 1.697 3.308 3.289 5.049 "
                  "4.845 3.248 2.189-5.438 1.289-8.678 1.284-5.428-.061-10.825-.463-11.992-.892zm12.74-3.242c-1.123-.694-2.36"
                  "-.943-2.75-.554-.389.39.21 1.275 1.334 1.97 1.122.693 2.36.942 2.749.553.389-.39-.21-1.275-1.334-1.97zm"
                  "-5.663 0a1.42 1.42 0 00-1.415-1.416 1.42 1.42 0 00-1.416 1.416 1.42 1.42 0 001.416 1.415 1.42 1.42 0 001"
                  ".415-1.415zm-8.464-6.404c.984-1.187 1.35-2.598.813-3.135-1.181-1.18-5.408 1.297-6.184 3.624-.806 2.42 "
                  "3.265 2.048 5.37-.49zm6.863.258c.867-1.045 1.163-2.313.658-2.819-1.063-1.062-4.719 1.631-4.719 3.476 0 "
                  "1.864 2.274 1.496 4.061-.657zm8.792-.36c1.637-1.972 1.448-2.197-1.486-1.77-1.848.27-3.622 1.287-3.943 2.26"
                  "-.838 2.547 3.212 2.181 5.429-.49zm32.443-4.11c-6.156-2.228-67.1-6.138-119.124-7.642-39.208-1.134-72.072"
                  "-.928-94.618.593-6.617.446-19.681 1.16-29.03 1.587-15.798.72-17.183.573-19.588-2.085-4.498-4.97-2.544-7.857 "
                  "6.39-9.44 4.394-.778 9.164-2.436 10.6-3.685 5.44-4.729 20.332-14.06 31.14-19.509C65.717 11.88 78.955 7.79 "
                  "103.837 3.08 121.686-.3 125.552-.642 129.318.82c2.44.948 12.4 1.948 22.132 2.221 15.37.432 20.004 1.18 "
                  "35.294 5.698 22.36 6.606 39.732 15.1 56.55 27.653 7.307 5.452 14.086 9.913 15.066 9.913.98 0 2.148.956 "
                  "2.596 2.124.55 1.432 2.798 2.123 6.914 2.123 6.213 0 12.4 3.046 12.38 6.096-.012 1.75-6.502 5.353-9.118 "
                  "5.063-.818-.09-3.717-.972-6.442-1.958zm-16.986-7.436c0-1.575-33.326-18.118-43.173-21.43-23.008-7.739-54.084"
                  "-12.922-77.136-12.866-16.863.041-37.877 3.628-52.465 8.956-18.062 6.596-26.563 10.384-29.181 13.002-1.205 "
                  "1.205-5.306 3.769-9.112 5.698-7.754 3.929-8.841 5.482-3.029 4.325 13.494-2.685 66.794-3.773 110.913-2.264 "
                  "38.005 1.3 96.812 4.435 102.122 5.443.584.111 1.061-.277 1.061-.864zm-236.39-3.18c0-.78-1.592-1.416-3.539"
                  "-1.416-1.946 0-3.538.637-3.538 1.415 0 .779 1.592 1.416 3.538 1.416 1.947 0 3.54-.637 3.54-1.416zm7.078"
                  "-1.416c0-.779-.956-1.416-2.124-1.416-1.167 0-2.123.637-2.123 1.416 0 .778.956 1.415 2.123 1.415 1.168 0 "
                  "2.124-.637 2.124-1.415zm11.734-4.437c3.278-1.661 6.278-3.483 6.667-4.048 1.366-1.98 20.645-11.231 32.557"
                  "-15.622 11.862-4.372 36.546-9.865 44.327-9.865 3.485 0 3.867-.404 3.012-3.185-.538-1.752-1.177-3.41-1.42"
                  "-3.685-.907-1.026-36.72 7.16-45.065 10.302-17.226 6.484-47.566 24.27-47.566 27.886 0 1.786.845 1.585 7.488"
                  "-1.783zm206.254-5.577c-12.298-10.518-53.842-27.166-70.896-28.41-5.526-.404-6.3-.097-6.695 2.655-.33 2.307"
                  ".402 3.275 2.831 3.742 32.436 6.237 52.205 12.315 66.975 20.594 11.904 6.673 14.477 7.141 7.785 1.419z"
                  "M150.1 11.04c-1.949-3.64-7.568-4.078-6.886-.538.256 1.329 2.054 2.817 3.997 3.309 4.498 1.137 4.816.832 "
                  "2.888-2.771zm6.756.94c-.248-1.752-1.026-3.185-1.727-3.185-.7 0-1.493 1.433-1.76 3.185-.328 2.152.232 "
                  "3.185 1.727 3.185 1.485 0 2.064-1.047 1.76-3.185zm-30.178-2.458c0-2.303-.908-3.694-2.627-4.025-3.6-.694"
                  "-5.23 1.301-4.22 5.166 1.216 4.647 6.847 3.709 6.847-1.14zm12.544 2.104c-.448-1.168-1.224-2.132-1.725"
                  "-2.142-.5-.013-2.343-.404-4.095-.873-2.569-.689-3.185-.274-3.185 2.142 0 2.476.854 2.996 4.91 2.996 "
                  "3.783 0 4.723-.487 4.095-2.123z")

        result1 (->> (svg.path/parse data)
                     (mapv (fn [entry]
                             (update entry :params #(into (sorted-map) %)))))
        result2 (->> (svg.path.legacy/parse data)
                     (mapv (fn [entry]
                            (update entry :params #(into (sorted-map) %)))))]

    (t/is (= 165
             (count result1)
             (count result2)))


    (dotimes [i (count result1)]
      (let [item1 (nth result1 i)
            item2 (nth result2 i)]

        (t/is (= (:command item1)
                 (:command item2)))


        ;; (println "================" (:command item1))
        ;; (pp/pprint (:params item1))
        ;; (println "---------")
        ;; (pp/pprint (:params item2))

        (doseq [[k v] (:params item1)]
          (t/is (mth/close? v (get-in item2 [:params k]) 0.0000001)))))))


(t/deftest parse-test-3
  (let [data    (str "m-5.663 0a1.42 1.42 0 00-1.415-1.416 1.42 1.42 0 00-1.416 1.416 "
                     "1.42 1.42 0 001.416 1.415 1.42 1.42 0 001.415-1.415z")
        result1 (->> (svg.path/parse data)
                     (mapv (fn [entry]
                             (update entry :params #(into (sorted-map) %)))))
        result2 (->> (svg.path.legacy/parse data)
                     (mapv (fn [entry]
                            (update entry :params #(into (sorted-map) %)))))]

    (t/is (= 6
             (count result1)
             (count result2)))

    (dotimes [i (count result1)]
      (let [item1 (nth result1 i)
            item2 (nth result2 i)]

        (t/is (= (:command item1)
                 (:command item2)))

        (doseq [[k v] (:params item1)]
          (t/is (mth/close? v (get-in item2 [:params k]) 0.000000001)))))))


(t/deftest parse-test-4
  (let [data    (str "m480 839-41-37c-70.512-64.747-128.807-120.601-174.884-167.561C218.039 587.48 181.333 "
                     "545.5 154 508.5S107.5 438 96.5 408 80 347.667 80 317c0-60.103 20.167-110.296 "
                     "60.5-150.577C180.833 126.141 230.667 106 290 106c38 0 73.167 9 105.5 27s60.5 44 84.5 "
                     "78c28-36 57.667-62.5 89-79.5S634 106 670 106c59.333 0 109.167 20.141 149.5 60.423C859.833 "
                     "206.704 880 256.897 880 317c0 30.667-5.5 61-16.5 91s-30.167 63.5-57.5 100.5-64.039 "
                     "78.98-110.116 125.939C649.807 681.399 591.512 737.253 521 802l-41 37Zm0-79c67.491-61.997 "
                     "123.03-115.163 166.618-159.498C690.206 556.167 724.833 517.333 750.5 484s43.667-63.045 "
                     "54-89.135c10.333-26.091 15.5-51.997 15.5-77.72 0-44.097-14-80.312-42-108.645S714.075 166 "
                     "670.225 166c-34.349 0-66.141 10.5-95.375 31.5C545.617 218.5 522 248 504 286h-49c-17.333"
                     "-37.333-40.617-66.667-69.85-88-29.234-21.333-61.026-32-95.375-32C245.925 166 210 180.167 "
                     "182 208.5s-42 64.605-42 108.816c0 25.789 5.167 51.851 15.5 78.184s28.333 56.333 54 90S270 "
                     "558 314 602s99.333 96.667 166 158Zm0-297Z")

        expect  [{:command :move-to, :params {:x 480.0, :y 839.0}}
                 {:command :line-to, :params {:x 439.0, :y 802.0}}
                 {:command :curve-to, :params {:c1x 368.488, :c1y 737.253, :c2x 310.193, :c2y 681.399, :x 264.116, :y 634.439}}
                 {:command :curve-to, :params {:c1x 218.039, :c1y 587.48, :c2x 181.333, :c2y 545.5, :x 154.0, :y 508.5}}
                 {:command :curve-to, :params {:c1x 126.667, :c1y 471.5, :c2x 107.5, :c2y 438.0, :x 96.5, :y 408.0}}
                 {:command :curve-to, :params {:c1x 85.5, :c1y 378.0, :c2x 80.0, :c2y 347.667, :x 80.0, :y 317.0}}
                 {:command :curve-to, :params {:c1x 80.0, :c1y 256.897, :c2x 100.167, :c2y 206.704, :x 140.5, :y 166.423}}
                 {:command :curve-to, :params {:c1x 180.833, :c1y 126.141, :c2x 230.667, :c2y 106.0, :x 290.0, :y 106.0}}
                 {:command :curve-to, :params {:c1x 328.0, :c1y 106.0, :c2x 363.16700000000003, :c2y 115.0, :x 395.5, :y 133.0}}
                 {:command :curve-to, :params {:c1x 427.83299999999997, :c1y 151.0, :c2x 456.0, :c2y 177.0, :x 480.0, :y 211.0}}
                 {:command :curve-to, :params {:c1x 508.0, :c1y 175.0, :c2x 537.667, :c2y 148.5, :x 569.0, :y 131.5}}
                 {:command :curve-to, :params {:c1x 600.333, :c1y 114.5, :c2x 634.0, :c2y 106.0, :x 670.0, :y 106.0}}
                 {:command :curve-to, :params {:c1x 729.333, :c1y 106.0, :c2x 779.167, :c2y 126.14099999999999, :x 819.5, :y 166.423}}
                 {:command :curve-to, :params {:c1x 859.833, :c1y 206.704, :c2x 880.0, :c2y 256.897, :x 880.0, :y 317.0}}
                 {:command :curve-to, :params {:c1x 880.0, :c1y 347.66700000000003, :c2x 874.5, :c2y 378.0, :x 863.5, :y 408.0}}
                 {:command :curve-to, :params {:c1x 852.5, :c1y 438.0, :c2x 833.333, :c2y 471.5, :x 806.0, :y 508.5}}
                 {:command :curve-to, :params {:c1x 778.667, :c1y 545.5, :c2x 741.961, :c2y 587.48, :x 695.884, :y 634.439}}
                 {:command :curve-to, :params {:c1x 649.807, :c1y 681.399, :c2x 591.512, :c2y 737.253, :x 521.0, :y 802.0}}
                 {:command :line-to, :params {:x 480.0, :y 839.0}}
                 {:command :close-path, :params {}}
                 {:command :move-to, :params {:x 480.0, :y 760.0}}
                 {:command :curve-to, :params {:c1x 547.491, :c1y 698.003, :c2x 603.03, :c2y 644.837, :x 646.6179999999999, :y 600.502}}
                 {:command :curve-to, :params {:c1x 690.206, :c1y 556.167, :c2x 724.833, :c2y 517.333, :x 750.5, :y 484.0}}
                 {:command :curve-to, :params {:c1x 776.167, :c1y 450.66700000000003, :c2x 794.167, :c2y 420.955, :x 804.5, :y 394.865}}
                 {:command :curve-to, :params {:c1x 814.833, :c1y 368.774, :c2x 820.0, :c2y 342.868, :x 820.0, :y 317.145}}
                 {:command :curve-to, :params {:c1x 820.0, :c1y 273.048, :c2x 806.0, :c2y 236.83299999999997, :x 778.0, :y 208.5}}
                 {:command :curve-to, :params {:c1x 750.0, :c1y 180.16700000000003, :c2x 714.075, :c2y 166.0, :x 670.225, :y 166.0}}
                 {:command :curve-to, :params {:c1x 635.876, :c1y 166.0, :c2x 604.0840000000001, :c2y 176.5, :x 574.85, :y 197.5}}
                 {:command :curve-to, :params {:c1x 545.617, :c1y 218.5, :c2x 522.0, :c2y 248.0, :x 504.0, :y 286.0}}
                 {:command :line-to, :params {:x 455.0, :y 286.0}}
                 {:command :curve-to, :params {:c1x 437.66700000000003, :c1y 248.667, :c2x 414.383, :c2y 219.333, :x 385.15, :y 198.0}}
                 {:command :curve-to, :params {:c1x 355.916, :c1y 176.667, :c2x 324.12399999999997, :c2y 166.0, :x 289.775, :y 166.0}}
                 {:command :curve-to, :params {:c1x 245.925, :c1y 166.0, :c2x 210.0, :c2y 180.167, :x 182.0, :y 208.5}}
                 {:command :curve-to, :params {:c1x 154.0, :c1y 236.833, :c2x 140.0, :c2y 273.105, :x 140.0, :y 317.31600000000003}}
                 {:command :curve-to, :params {:c1x 140.0, :c1y 343.105, :c2x 145.167, :c2y 369.16700000000003, :x 155.5, :y 395.5}}
                 {:command :curve-to,
                  :params {:c1x 165.833, :c1y 421.83299999999997, :c2x 183.833, :c2y 451.83299999999997, :x 209.5, :y 485.5}}
                 {:command :curve-to, :params {:c1x 235.167, :c1y 519.167, :c2x 270.0, :c2y 558.0, :x 314.0, :y 602.0}}
                 {:command :curve-to, :params {:c1x 358.0, :c1y 646.0, :c2x 413.33299999999997, :c2y 698.667, :x 480.0, :y 760.0}}
                 {:command :close-path, :params {}}
                 {:command :move-to, :params {:x 480.0, :y 463.0}}
                 {:command :close-path, :params {}}]

        result1 (->> (svg.path/parse data)
                     (mapv (fn [entry]
                             (update entry :params #(into (sorted-map) %)))))
        result2 (->> (svg.path.legacy/parse data)
                     (mapv (fn [entry]
                            (update entry :params #(into (sorted-map) %)))))]

    (t/is (= 41
             (count result1)
             (count result2)))

    ;; (pp/pprint result1 {:length 50})

    (dotimes [i (count result1)]
      (let [item1 (nth result1 i)
            item2 (nth result2 i)
            item3 (nth expect i)]

        (t/is (= (:command item1)
                 (:command item2)
                 (:command item3)))

        (doseq [[k v] (:params item1)]
          (t/is (mth/close? v (get-in item2 [:params k]) 0.000000001))
          (t/is (mth/close? v (get-in item3 [:params k]) 0.000000001))
          )))))

(t/deftest parse-test-5
  (let [data    (str "M363 826"
                     "q11-56 54-93"
                     "t101-37"
                     "h176"
                     "q22-35 34-75.179 12-40.178 12-84.821 0-125.357-87.321-212.679"
                     "Q565.357 236 440 236"
                     "t-212.679 87.321"
                     "Q140 410.643 140 536"
                     "q0 105 63 184.5T363 826Zm157 190"
                     "q-58 0-102"
                     "-36.5T363 888q-122-26-202.5-124T80 536q0-150 105-255t255-105"
                     "q150 0 255 105t105 "
                     "255q0 43-9.5 83.5T763 696q66 0 111.5 47T920 856q0 66-47 113t-113 47H520Zm-80"
                     "-485Zm200 325ZM520 956h240q42 0 71-29t29-71q0-42-29-71t-71-29H520q-42 0-71 29t"
                     "-29 71q0 42 29 71t71 29Zm-.175-70Q507 886 498.5 877.325"
                     "q-8.5-8.676-8.5-21.5 0"
                     "-12.825 8.675-21.325 8.676-8.5 21.5-8.5 12.825 0 21.325 8.675 8.5 8.676 8.5 "
                     "21.5 0 12.825-8.675 21.325-8.676 8.5-21.5 8.5Zm120 0Q627 886 618.5 877.325q-8.5"
                     "-8.676-8.5-21.5 0-12.825 8.675-21.325 8.676-8.5 21.5-8.5 12.825 0 21.325 8.675 "
                     "8.5 8.676 8.5 21.5 0 12.825-8.675 21.325-8.676 8.5-21.5 8.5Zm120 0Q747 886 "
                     "738.5 877.325q-8.5-8.676-8.5-21.5 0-12.825 8.675-21.325 8.676-8.5 21.5-8.5 "
                     "12.825 0 21.325 8.675 8.5 8.676 8.5 21.5 0 12.825-8.675 21.325-8.676 8.5-21.5 "
                     "8.5Z"
                     )

        result1 (->> (svg.path/parse data)
                     (mapv (fn [entry]
                             (update entry :params #(into (sorted-map) %)))))
        result2 (->> (svg.path.legacy/parse data)
                     (mapv (fn [entry]
                            (update entry :params #(into (sorted-map) %)))))]

    (t/is (= 76
             (count result1)
             (count result2)))

    ;; (pp/pprint result1 {:length 100})
    ;; (pp/pprint result2 {:length 50})

    (dotimes [i (count result1)]
      (let [item1 (nth result1 i)
            item2 (nth result2 i)
            ]

        (t/is (= (:command item1)
                 (:command item2)))

        (doseq [[k v] (:params item1)]
          (t/is (mth/close? v (get-in item2 [:params k]) 0.000000001))
          )))))

(t/deftest arc-to-bezier-1
  (let [expected1 [-1.6697754290362354e-13
                   -5.258016244624741e-13
                   182.99396814652343
                   578.9410968299095
                   338.05561855139365
                   1059.4584670906731
                   346.33988979885567
                   1073.265585836443]
        expected2 [346.33988979885567
                   1073.265585836443
                   354.6241610463177
                   1087.0727045822134
                   212.99396814652377
                   628.9410968299106
                   30.00000000000016
                   50.000000000000504]]

    (let [[result1 result2 :as total] (->> (svg.path/arc->beziers 0 0 30 50 0 0 1 162.55 162.45)
                                           (mapv (fn [segment]
                                                   (vec (.-params segment)))))]
      ;; (t/is (= (count total) 2))
      ;; (println "================" 11111111)
      ;; (pp/pprint expected1 {:width 50})
      ;; (println "------------")
      ;; (pp/pprint result1 {:width 50})

      (dotimes [i (count result1)]
        (t/is (mth/close? (nth result1 i)
                          (nth expected1 (+ i 2))
                          0.0000000001)))

      (dotimes [i (count result2)]
        (t/is (mth/close? (nth result2 i)
                          (nth expected2 (+ i 2))
                          0.0000000001))))

    (let [[result1 result2 :as total] (svg.path.legacy/arc->beziers* 0 0 30 50 0 0 1 162.55 162.45)]
      (t/is (= (count total) 2))

      (dotimes [i (count result1)]
        (t/is (mth/close? (nth result1 i)
                          (nth expected1 i)
                          0.000000000001)))

      (dotimes [i (count result2)]
        (t/is (mth/close? (nth result2 i)
                          (nth expected2 i)
                          0.000000000001))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; LEGACY CODE TESTS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(t/deftest extract-params-legacy-1
  (let [expected [{:x -994.563, :y 4564.1423}
                  {:x 149.3086, :y -52.8821}
                  {:x 30.1828, :y -1.9265}
                  {:x 5.2446, :y -117.5157}
                  {:x 98.6828, :y -43.7312}
                  {:x 219.9492, :y 9.5361}
                  {:x 9.0977, :y 121.0797}
                  {:x 115.0586, :y 12.7148}
                  {:x -1.1774, :y 75.7109}
                  {:x 134.7524, :y 3.1787}
                  {:x -6.1008, :y 85.0544}
                  {:x -137.3211, :y 59.9137}
                  {:x -301.293, :y -1.0595}
                  {:x -51.375, :y 25.7186}
                  {:x -261.0492, :y -7.706}]
        cmdstr (str "m -994.563 4564.1423 149.3086 -52.8821 30.1828 "
                    "-1.9265 5.2446 -117.5157 98.6828 -43.7312 219.9492 "
                    "9.5361 9.0977 121.0797 115.0586 12.7148 -1.1774 "
                    "75.7109 134.7524 3.1787 -6.1008 85.0544 -137.3211 "
                    "59.9137 -301.293 -1.0595 -51.375 25.7186 -261.0492 -7.706 ")
        pattern [[:x :number] [:y :number]]]

    (t/is (= expected (svg.path.legacy/extract-params cmdstr pattern)))))

(t/deftest extract-params-legacy-2
  (let [expected [{:x -994.563, :y 4564.1423 :r 0}]
        cmdstr (str "m -994.563 4564.1423 0")
        pattern [[:x :number] [:y :number] [:r :flag]]]

    (t/is (= expected (svg.path.legacy/extract-params cmdstr pattern)))))

(t/deftest extract-params-legacy-3
  (let [cmdstr   (str "a1.42 1.42 0 00-1.415-1.416 1.42 1.42 0 00-1.416 1.416 "
                      "1.42 1.42 0 001.416 1.415 1.42 1.42 0 001.415-1.415")

        expected [{:rx 1.42, :ry 1.42, :x-axis-rotation 0.0, :large-arc-flag 0, :sweep-flag 0, :x -1.415, :y -1.416}
                  {:rx 1.42, :ry 1.42, :x-axis-rotation 0.0, :large-arc-flag 0, :sweep-flag 0, :x -1.416, :y 1.416}
                  {:rx 1.42, :ry 1.42, :x-axis-rotation 0.0, :large-arc-flag 0, :sweep-flag 0, :x 1.416, :y 1.415}
                  {:rx 1.42, :ry 1.42, :x-axis-rotation 0.0, :large-arc-flag 0, :sweep-flag 0, :x 1.415, :y -1.415}]

        pattern  [[:rx :number]
                  [:ry :number]
                  [:x-axis-rotation :number]
                  [:large-arc-flag :flag]
                  [:sweep-flag :flag]
                  [:x :number]
                  [:y :number]]
        result  (svg.path.legacy/extract-params cmdstr pattern)]

    (t/is (= (nth result 0)
             (nth expected 0)))
    (t/is (= (nth result 1)
             (nth expected 1)))
    (t/is (= (nth result 2)
             (nth expected 2)))
    (t/is (= (nth result 3)
             (nth expected 3)))
    ))

;; FOR POSSIBLE FUTURE TEST CASES
;; (str "M259.958 89.134c-6.88-.354-10.484-1.241-12.44-3.064-1.871-1.743-6.937-3.098-15.793-4.226-7.171-.913-17.179-2.279-22.24-3.034-5.06-.755-15.252-2.016-22.648-2.8-18.685-1.985-35.63-4.223-38.572-5.096-3.655-1.084-3.016-3.548.708-2.726 1.751.387 13.376 1.701 25.833 2.922 12.456 1.22 29.018 3.114 36.803 4.208 29.94 4.206 29.433 4.204 34.267.136 3.787-3.186 5.669-3.669 14.303-3.669 14.338 0 17.18 1.681 12.182 7.205-2.053 2.268-1.994 2.719.707 5.42 3.828 3.827 3.74 5.846-.238 5.5-1.752-.153-7.544-.502-12.872-.776zm7.563-3.194c0-.778-1.751-1.352-3.892-1.274l-3.893.141 3.539 1.133c1.946.624 3.698 1.197 3.893 1.275.194.077.354-.496.354-1.275zm-15.899-8.493c1.43-2.29 1.414-2.83-.084-2.83-2.05 0-5.25 2.76-5.25 4.529 0 2.226 3.599 1.08 5.334-1.699zm8.114 0c2.486-2.746 2.473-2.83-.438-2.83-1.65 0-3.683 1.273-4.516 2.83-1.175 2.196-1.077 2.831.438 2.831 1.075 0 3.107-1.274 4.516-2.83zm7.814.674c2.858-3.444.476-4.085-3.033-.816-2.451 2.284-2.677 2.973-.975 2.973 1.22 0 3.023-.97 4.008-2.157zm-49.571-4.509c-1.168-.43-3.294-1.802-4.725-3.051-2.112-1.843-9.304-2.595-38.219-3.994-46.474-2.25-63-4.077-60.27-6.665.324-.308 9.507.261 20.406 1.264 10.9 1.003 31.16 2.258 45.024 2.789l25.207.964 4.625-3.527c4.313-3.29 5.41-3.474 16.24-2.732 6.389.438 11.981 1.388 12.428 2.111.447.723-.517 2.73-2.141 4.46l-2.954 3.144c1.607 1.697 3.308 3.289 5.049 4.845 3.248 2.189-5.438 1.289-8.678 1.284-5.428-.061-10.825-.463-11.992-.892zm12.74-3.242c-1.123-.694-2.36-.943-2.75-.554-.389.39.21 1.275 1.334 1.97 1.122.693 2.36.942 2.749.553.389-.39-.21-1.275-1.334-1.97zm-5.663 0a1.42 1.42 0 00-1.415-1.416 1.42 1.42 0 00-1.416 1.416 1.42 1.42 0 001.416 1.415 1.42 1.42 0 001.415-1.415zm-8.464-6.404c.984-1.187 1.35-2.598.813-3.135-1.181-1.18-5.408 1.297-6.184 3.624-.806 2.42 3.265 2.048 5.37-.49zm6.863.258c.867-1.045 1.163-2.313.658-2.819-1.063-1.062-4.719 1.631-4.719 3.476 0 1.864 2.274 1.496 4.061-.657zm8.792-.36c1.637-1.972 1.448-2.197-1.486-1.77-1.848.27-3.622 1.287-3.943 2.26-.838 2.547 3.212 2.181 5.429-.49zm32.443-4.11c-6.156-2.228-67.1-6.138-119.124-7.642-39.208-1.134-72.072-.928-94.618.593-6.617.446-19.681 1.16-29.03 1.587-15.798.72-17.183.573-19.588-2.085-4.498-4.97-2.544-7.857 6.39-9.44 4.394-.778 9.164-2.436 10.6-3.685 5.44-4.729 20.332-14.06 31.14-19.509C65.717 11.88 78.955 7.79 103.837 3.08 121.686-.3 125.552-.642 129.318.82c2.44.948 12.4 1.948 22.132 2.221 15.37.432 20.004 1.18 35.294 5.698 22.36 6.606 39.732 15.1 56.55 27.653 7.307 5.452 14.086 9.913 15.066 9.913.98 0 2.148.956 2.596 2.124.55 1.432 2.798 2.123 6.914 2.123 6.213 0 12.4 3.046 12.38 6.096-.012 1.75-6.502 5.353-9.118 5.063-.818-.09-3.717-.972-6.442-1.958zm-16.986-7.436c0-1.575-33.326-18.118-43.173-21.43-23.008-7.739-54.084-12.922-77.136-12.866-16.863.041-37.877 3.628-52.465 8.956-18.062 6.596-26.563 10.384-29.181 13.002-1.205 1.205-5.306 3.769-9.112 5.698-7.754 3.929-8.841 5.482-3.029 4.325 13.494-2.685 66.794-3.773 110.913-2.264 38.005 1.3 96.812 4.435 102.122 5.443.584.111 1.061-.277 1.061-.864zm-236.39-3.18c0-.78-1.592-1.416-3.539-1.416-1.946 0-3.538.637-3.538 1.415 0 .779 1.592 1.416 3.538 1.416 1.947 0 3.54-.637 3.54-1.416zm7.078-1.416c0-.779-.956-1.416-2.124-1.416-1.167 0-2.123.637-2.123 1.416 0 .778.956 1.415 2.123 1.415 1.168 0 2.124-.637 2.124-1.415zm11.734-4.437c3.278-1.661 6.278-3.483 6.667-4.048 1.366-1.98 20.645-11.231 32.557-15.622 11.862-4.372 36.546-9.865 44.327-9.865 3.485 0 3.867-.404 3.012-3.185-.538-1.752-1.177-3.41-1.42-3.685-.907-1.026-36.72 7.16-45.065 10.302-17.226 6.484-47.566 24.27-47.566 27.886 0 1.786.845 1.585 7.488-1.783zm206.254-5.577c-12.298-10.518-53.842-27.166-70.896-28.41-5.526-.404-6.3-.097-6.695 2.655-.33 2.307.402 3.275 2.831 3.742 32.436 6.237 52.205 12.315 66.975 20.594 11.904 6.673 14.477 7.141 7.785 1.419zM150.1 11.04c-1.949-3.64-7.568-4.078-6.886-.538.256 1.329 2.054 2.817 3.997 3.309 4.498 1.137 4.816.832 2.888-2.771zm6.756.94c-.248-1.752-1.026-3.185-1.727-3.185-.7 0-1.493 1.433-1.76 3.185-.328 2.152.232 3.185 1.727 3.185 1.485 0 2.064-1.047 1.76-3.185zm-30.178-2.458c0-2.303-.908-3.694-2.627-4.025-3.6-.694-5.23 1.301-4.22 5.166 1.216 4.647 6.847 3.709 6.847-1.14zm12.544 2.104c-.448-1.168-1.224-2.132-1.725-2.142-.5-.013-2.343-.404-4.095-.873-2.569-.689-3.185-.274-3.185 2.142 0 2.476.854 2.996 4.91 2.996 3.783 0 4.723-.487 4.095-2.123z")

