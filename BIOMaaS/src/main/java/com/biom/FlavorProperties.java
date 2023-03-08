package com.biom;
import java.util.ArrayList;
import java.util.List;

public class FlavorProperties {
	
	// maximum proteins a vm can hold
    final int SMALL_MAX = 17;
    final int MEDIUM_MAX = 15;
    final int LARGE_MAX = 10;
    
    
 // vacant VMs list
    List<VmProperties> vacant_vms = new ArrayList<>();

    // vm flavor
    private int flavor;
    
	// vacant_vms_capacity
    private int vacant_vm_cap;
    
    // max_capacity
    private final int max_capacity;
    
    public FlavorProperties(int flavor_id) {
    	if (flavor_id == 10) {
    		this.max_capacity = SMALL_MAX;
    		this.flavor = 10;
    	} else if(flavor_id == 11) {
    		this.max_capacity = MEDIUM_MAX;
    		this.flavor = 11;
    	} else {
    		this.max_capacity = LARGE_MAX;
    		this.flavor = 12;
    	}
    	this.vacant_vm_cap = 0;
    }
    
    public int getFlavor() {
    	return flavor;
    }
    
    public int getMaxCapacity() {
    	return max_capacity;
    }
    
    public void addVacantVm(VmProperties vm) {
    	vacant_vms.add(vm);
    }
    
	public int getVacant_vm_cap() {
		return vacant_vm_cap;
	}

	public void setVacant_vm_cap(int vacant_vm_cap) {
		this.vacant_vm_cap = vacant_vm_cap;
	}

    
    


}
