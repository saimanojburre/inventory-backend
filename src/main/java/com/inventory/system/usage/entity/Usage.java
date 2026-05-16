package com.inventory.system.usage.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.inventory.system.item.entity.Item;
import com.inventory.system.user.entity.User;

import jakarta.persistence.*;

@Entity
@Table(
        name = "usage_records",
        indexes = {
                @Index(name = "idx_usage_item", columnList = "item_id"),
                @Index(name = "idx_usage_date", columnList = "usedDateTime"),
                @Index(name = "idx_usage_department", columnList = "department")
        }
)
public class Usage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "item_id")
	private Item item;

	private BigDecimal quantity;

	private String department;

	private LocalDateTime usedDateTime;

	private String takenBy;

	private String givenBy;

	@Column(name = "cost_per_unit")
	private BigDecimal costPerUnit;

	@Column(name = "total_cost")
	private BigDecimal totalCost;

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "created_by")
	private User createdBy;

	private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "updated_by")
	private User updatedBy;

	private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public LocalDateTime getUsedDateTime() {
		return usedDateTime;
	}

	public void setUsedDateTime(LocalDateTime usedDateTime) {
		this.usedDateTime = usedDateTime;
	}

	public String getTakenBy() {
		return takenBy;
	}

	public void setTakenBy(String takenBy) {
		this.takenBy = takenBy;
	}

	public String getGivenBy() {
		return givenBy;
	}

	public void setGivenBy(String givenBy) {
		this.givenBy = givenBy;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public BigDecimal getCostPerUnit() {
		return costPerUnit;
	}

	public void setCostPerUnit(BigDecimal costPerUnit) {
		this.costPerUnit = costPerUnit;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

}