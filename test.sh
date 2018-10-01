#!/bin/bash
declare -a arr=("fred1" "wilma1" "barney1" "betty1")

	for i in "${arr[@]}"
	do
		echo "Serving beer to $i ------------------------------------------------------------"
		for j in {1..10} ; do
			curl -H "Accept:application/json"  http://localhost:8082/waiter/beer/${i}
			echo
		done

		echo "Billing $i ------------------------------------------------------------"
	 	curl -H "Accept:application/json"  http://localhost:8082/waiter/bill/${i}
	 	curl -H "Accept:application/json"  http://localhost:8082/waiter/bill/${i}
	 	curl -H "Accept:application/json"  http://localhost:8082/waiter/bill/${i}
	 	curl -H "Accept:application/json"  http://localhost:8082/waiter/bill/${i}
		echo

	done
