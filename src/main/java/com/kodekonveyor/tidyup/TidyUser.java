package com.kodekonveyor.tidyup;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.JoinColumn;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
public class TidyUser {
	
	@Id @GeneratedValue
	private Long id;
	
	private String email;
	
	@JsonIgnore
	private String password;
	
    @ManyToMany
    // @JoinTable(name = "tidyusers_roles", joinColumns = @JoinColumn(name = "tidyuser_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Collection<WorkRequest> workRequests;

}
