# ID3

A Clojure library implementing Quinlans well-known Iterative Dichotomiser 3, aka "Induction of Decision Trees".

## Installation
[![Clojars Project](https://img.shields.io/clojars/v/net.clojars.oyvinht/id3.svg)](https://clojars.org/net.clojars.oyvinht/id3)

## Usage

```

(def examples
  [{:outlook "sunny"    :temp "hot"  :humidity "high"   :wind "weak"   :play-tennis "no"}
   {:outlook "sunny"    :temp "hot"  :humidity "high"   :wind "strong" :play-tennis "no"}
   {:outlook "overcast" :temp "hot"  :humidity "high"   :wind "weak"   :play-tennis "yes"}
   {:outlook "rain"     :temp "mild" :humidity "high"   :wind "weak"   :play-tennis "yes"}
   {:outlook "rain"     :temp "cool" :humidity "normal" :wind "weak"   :play-tennis "yes"}
   {:outlook "rain"     :temp "cool" :humidity "normal" :wind "strong" :play-tennis "no"}
   {:outlook "overcast" :temp "cool" :humidity "normal" :wind "strong" :play-tennis "yes"}
   {:outlook "sunny"    :temp "mild" :humidity "high"   :wind "weak"   :play-tennis "no"}
   {:outlook "sunny"    :temp "cool" :humidity "normal" :wind "weak"   :play-tennis "yes"}
   {:outlook "rain"     :temp "mild" :humidity "normal" :wind "weak"   :play-tennis "yes"}
   {:outlook "sunny"    :temp "mild" :humidity "normal" :wind "strong" :play-tennis "yes"}
   {:outlook "overcast" :temp "mild" :humidity "high"   :wind "strong" :play-tennis "yes"}
   {:outlook "overcast" :temp "hot"  :humidity "normal" :wind "weak"   :play-tennis "yes"}
   {:outlook "rain"     :temp "mild" :humidity "high"   :wind "strong" :play-tennis "no"}])

(def dtree (id3 examples :play-tennis "yes" "no"))

(def instance {:humidity "high" :outlook "overcast" :temp "hot" :wind "weak"})

(classify instance dtree)

;;;; -> "yes"

```

Copyright © 2021 Øyvin Halfdan Thuv

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
